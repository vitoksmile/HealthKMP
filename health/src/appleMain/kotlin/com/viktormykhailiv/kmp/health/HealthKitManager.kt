@file:OptIn(UnsafeNumber::class)

package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.HealthDataType.BodyTemperature
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.records.ExerciseSessionRecord
import com.viktormykhailiv.kmp.health.region.RegionalPreferences
import com.viktormykhailiv.kmp.health.region.TemperatureRegionalPreference
import kotlinx.cinterop.UnsafeNumber
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.time.Instant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSSortDescriptor
import platform.HealthKit.HKAuthorizationRequestStatusUnnecessary
import platform.HealthKit.HKCategorySample
import platform.HealthKit.HKDevice
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKObject
import platform.HealthKit.HKObjectQueryNoLimit
import platform.HealthKit.HKQuantitySample
import platform.HealthKit.HKQuantityType
import platform.HealthKit.HKQuery
import platform.HealthKit.HKQueryOptionStrictStartDate
import platform.HealthKit.HKSampleQuery
import platform.HealthKit.HKSampleSortIdentifierEndDate
import platform.HealthKit.HKSampleType
import platform.HealthKit.HKStatistics
import platform.HealthKit.HKStatisticsOptions
import platform.HealthKit.HKStatisticsQuery
import platform.HealthKit.HKUnit
import platform.HealthKit.HKWorkout
import platform.HealthKit.HKWorkoutRoute
import platform.HealthKit.HKWorkoutRouteBuilder
import platform.HealthKit.degreeCelsiusUnit
import platform.HealthKit.degreeFahrenheitUnit
import platform.HealthKit.predicateForSamplesWithStartDate
import platform.HealthKit.preferredUnitsForQuantityTypes
import kotlin.collections.map
import kotlin.collections.orEmpty
import kotlin.coroutines.resumeWithException

internal class HealthKitManager : HealthManager {

    private val healthStore by lazy { HKHealthStore() }

    override fun isAvailable(): Result<Boolean> = runCatching {
        HKHealthStore.isHealthDataAvailable()
    }

    override suspend fun isAuthorized(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        healthStore.getRequestStatusForAuthorizationToShareTypes(
            typesToShare = writeTypes.map { it.toHKSampleType() }.flatten().filterNotNull().toSet(),
            readTypes = readTypes.map { it.toHKSampleType() }.flatten().filterNotNull().toSet(),
        ) { status, error ->
            if (continuation.isCancelled) return@getRequestStatusForAuthorizationToShareTypes

            if (error != null) {
                continuation.resume(Result.failure(Throwable(error.toString())))
            } else {
                continuation.resume(Result.success(status == HKAuthorizationRequestStatusUnnecessary))
            }
        }
    }

    override suspend fun requestAuthorization(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        healthStore.requestAuthorizationToShareTypes(
            typesToShare = writeTypes.map { it.toHKSampleType() }.flatten().filterNotNull().toSet(),
            readTypes = readTypes.map { it.toHKSampleType() }.flatten().filterNotNull().toSet(),
        ) { _, error ->
            if (continuation.isCancelled) return@requestAuthorizationToShareTypes

            if (error == null) {
                // We don't case about result here, it will be mapped to isAuthorized
                continuation.resume(Result.success(Unit))
            } else {
                continuation.resume(Result.failure(Throwable(error.toString())))
            }
        }
    }.mapCatching {
        isAuthorized(readTypes = readTypes, writeTypes = writeTypes).getOrThrow()
    }

    override suspend fun isRevokeAuthorizationSupported(): Result<Boolean> =
        Result.success(false)

    override suspend fun revokeAuthorization(): Result<Unit> =
        Result.failure(NotImplementedError())

    @Suppress("UNCHECKED_CAST")
    override suspend fun readData(
        startTime: Instant,
        endTime: Instant,
        type: HealthDataType,
    ): Result<List<HealthRecord>> = runCatching {
        val result = type.toHKSampleType()
            .map { sampleType ->
                readData(
                    startTime = startTime,
                    endTime = endTime,
                    sampleType = sampleType
                        ?: run {
                            return Result.failure(NotImplementedError("$type is not supported"))
                        },
                )
                    .getOrElse { return Result.failure(it) }
            }
            .flatten()

        when {
            result.isEmpty() -> {
                return@runCatching emptyList()
            }

            result.firstOrNull() is HKQuantitySample -> {
                val temperaturePreference = suspend { getTemperaturePreference() }

                (result as List<HKQuantitySample>).toHealthRecord(temperaturePreference)
            }

            result.firstOrNull() is HKCategorySample -> {
                (result as List<HKCategorySample>).toHealthRecords()
            }

            result.firstOrNull() is HKWorkout -> {
                (result as List<HKWorkout>).toHealthRecords(healthStore)
            }

            else -> {
                throw NotImplementedError("Result ${result.firstOrNull()} is not supported")
            }
        }
    }

    override suspend fun writeData(
        records: List<HealthRecord>,
    ): Result<Unit> {
        val data = withContext(Dispatchers.Default) {
            records.associateWith { it.toHKObjects() }
        }
        val objects = withContext(Dispatchers.Default) {
            data.values.filterNotNull().flatten()
        }

        return suspendCancellableCoroutine { continuation ->
            healthStore.saveObjects(objects) { _, error ->
                if (continuation.isCancelled) return@saveObjects

                if (error == null) {
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(Result.failure(Throwable(error.toString())))
                }
            }
        }.flatMap { postWriteData(data) }
    }

    override suspend fun aggregate(
        startTime: Instant,
        endTime: Instant,
        type: HealthDataType,
    ): Result<HealthAggregatedRecord> {
        if (type == Sleep) {
            // Sleep is not supported for aggregation, aggregate manually
            return readSleep(startTime = startTime, endTime = endTime)
                .mapCatching { it.aggregate(startTime = startTime, endTime = endTime) }
        }

        val temperaturePreference = suspend { getTemperaturePreference() }

        return runCatching { type.toHKQuantityType() }
            .getOrElse { return Result.failure(it) }
            .map { quantityType ->
                aggregate(
                    startTime = startTime,
                    endTime = endTime,
                    quantityType = quantityType
                        ?: run {
                            return Result.failure(NotImplementedError("$type is not supported"))
                        },
                    options = type.toHKStatisticOptions(),
                )
                    .getOrElse { return Result.failure(it) }
            }
            .toHealthAggregatedRecord(temperaturePreference)
            .let { aggregatedRecord ->
                if (aggregatedRecord != null) {
                    Result.success(aggregatedRecord)
                } else {
                    Result.failure(IllegalStateException("$type is not supported"))
                }
            }
    }

    override suspend fun getRegionalPreferences(): Result<RegionalPreferences> = runCatching {
        RegionalPreferences(
            temperature = getTemperaturePreference(),
        )
    }

    private suspend fun getTemperaturePreference(): TemperatureRegionalPreference = runCatching {
        val quantityType = BodyTemperature.toHKQuantityType().first()
            ?: throw IllegalArgumentException("HKQuantityType is not provided")
        return suspendCancellableCoroutine { continuation ->
            healthStore.preferredUnitsForQuantityTypes(setOf(quantityType)) { result, error ->
                if (continuation.isCancelled) return@preferredUnitsForQuantityTypes

                if (error != null) {
                    continuation.resumeWithException(Throwable(error.toString()))
                    return@preferredUnitsForQuantityTypes
                }

                if (result == null || result.isEmpty()) {
                    continuation.resumeWithException(IllegalStateException("Regional preferences data not found"))
                    return@preferredUnitsForQuantityTypes
                }

                val temperature = when (val unit = result[quantityType]) {
                    HKUnit.degreeCelsiusUnit() -> Result.success(TemperatureRegionalPreference.Celsius)
                    HKUnit.degreeFahrenheitUnit() -> Result.success(TemperatureRegionalPreference.Fahrenheit)
                    null -> Result.failure(IllegalStateException("Regional preferences data not found"))
                    else -> Result.failure(IllegalArgumentException("Temperature unit '$unit' not supported"))
                }
                temperature
                    .onSuccess { continuation.resume(it) }
                    .onFailure { continuation.resumeWithException(it) }
            }
        }
    }.getOrElse { TemperatureRegionalPreference.Fahrenheit }

    @Suppress("UNCHECKED_CAST")
    private suspend fun readData(
        startTime: Instant,
        endTime: Instant,
        sampleType: HKSampleType,
    ): Result<List<*>> = suspendCancellableCoroutine { continuation ->
        val query = HKSampleQuery(
            sampleType = sampleType,
            predicate = HKQuery.predicateForSamplesWithStartDate(
                startDate = startTime.toNSDate(),
                endDate = endTime.toNSDate(),
                options = HKQueryOptionStrictStartDate,
            ),
            limit = HKObjectQueryNoLimit,
            sortDescriptors = listOf(
                NSSortDescriptor(HKSampleSortIdentifierEndDate, ascending = false),
            ),
        ) { _, result, error ->
            if (continuation.isCancelled) return@HKSampleQuery

            if (error != null) {
                continuation.resume(Result.failure(Throwable(error.toString())))
                return@HKSampleQuery
            }

            when {
                result == null || result.isEmpty() -> {
                    continuation.resume(Result.success(emptyList<Any>()))
                }

                result.firstOrNull() is HKQuantitySample -> {
                    val records = result as List<HKQuantitySample>
                    continuation.resume(Result.success(records))
                }

                result.firstOrNull() is HKCategorySample -> {
                    val records = result as List<HKCategorySample>
                    continuation.resume(Result.success(records))
                }

                result.firstOrNull() is HKWorkout -> {
                    val records = result as List<HKWorkout>
                    continuation.resume(Result.success(records))
                }

                result.firstOrNull() is HKWorkoutRoute -> {
                    val records = result as List<HKWorkoutRoute>
                    continuation.resume(Result.success(records))
                }

                else -> {
                    continuation.resume(Result.failure(NotImplementedError("Result ${result.firstOrNull()} is not supported")))
                }
            }
        }

        healthStore.executeQuery(query)
    }

    private suspend fun postWriteData(
        data: Map<HealthRecord, List<HKObject>?>,
    ): Result<Unit> {
        val results = writeWorkoutMetadata(data)

        val failed = results.mapNotNull { it.exceptionOrNull() }
        return if (failed.isNotEmpty()) {
            Result.failure(
                Throwable(
                    "Failed to write workout metadata",
                    Throwable(failed.joinToString()),
                )
            )
        } else {
            Result.success(Unit)
        }
    }

    private suspend fun writeWorkoutMetadata(
        data: Map<HealthRecord, List<HKObject>?>,
    ): List<Result<Unit>> {
        val workouts = withContext(Dispatchers.Default) {
            data
                .asSequence()
                .filter { entry ->
                    entry.key is ExerciseSessionRecord
                }
                .filterNot { entry ->
                    (entry.key as ExerciseSessionRecord).exerciseRoute?.route.isNullOrEmpty()
                }
                .mapNotNull { entry ->
                    val record = entry.key as ExerciseSessionRecord
                    val workout = entry.value.orEmpty().firstOrNull() as? HKWorkout
                        ?: return@mapNotNull null
                    record to workout
                }
        }

        return workouts.toList()
            .map { (exercise, workout) ->
                writeWorkoutLocations(exercise, workout)
            }
    }

    private suspend fun writeWorkoutLocations(
        exercise: ExerciseSessionRecord,
        workout: HKWorkout,
    ): Result<Unit> {
        if (exercise.exerciseRoute?.route.isNullOrEmpty()) {
            return Result.success(Unit)
        }

        val builder = HKWorkoutRouteBuilder(
            healthStore = healthStore,
            device = HKDevice.localDevice(),
        )

        suspendCancellableCoroutine { continuation ->
            builder.insertRouteData(
                exercise.exerciseRoute.route.map { it.toCLLocation() },
            ) { _, error ->
                if (continuation.isCancelled) return@insertRouteData

                if (error == null) {
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(Result.failure(Throwable(error.toString())))
                }
            }
        }.onFailure {
            return Result.failure(it)
        }

        return suspendCancellableCoroutine { continuation ->
            builder.finishRouteWithWorkout(
                workout = workout,
                metadata = null,
            ) { _, error ->
                if (continuation.isCancelled) return@finishRouteWithWorkout

                if (error == null) {
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(Result.failure(Throwable(error.toString())))
                }
            }
        }
    }

    private suspend fun aggregate(
        startTime: Instant,
        endTime: Instant,
        quantityType: HKQuantityType,
        options: HKStatisticsOptions,
    ): Result<HKStatistics> = suspendCancellableCoroutine { continuation ->
        val query = HKStatisticsQuery(
            quantityType = quantityType,
            quantitySamplePredicate = HKQuery.predicateForSamplesWithStartDate(
                startDate = startTime.toNSDate(),
                endDate = endTime.toNSDate(),
                options = HKQueryOptionStrictStartDate,
            ),
            options = options,
        ) { _, result, error ->
            if (continuation.isCancelled) return@HKStatisticsQuery

            if (error != null) {
                continuation.resume(Result.failure(Throwable(error.toString())))
                return@HKStatisticsQuery
            }

            when {
                result == null -> {
                    continuation.resume(Result.failure(Throwable("$quantityType data not found")))
                }

                else -> {
                    continuation.resume(Result.success(result))
                }
            }
        }

        healthStore.executeQuery(query)
    }

}