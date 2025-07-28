@file:OptIn(UnsafeNumber::class)

package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.HealthDataType.BodyTemperature
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.region.RegionalPreferences
import com.viktormykhailiv.kmp.health.region.TemperatureRegionalPreference
import kotlinx.cinterop.UnsafeNumber
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.Instant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSSortDescriptor
import platform.HealthKit.HKAuthorizationRequestStatusUnnecessary
import platform.HealthKit.HKCategorySample
import platform.HealthKit.HKHealthStore
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
import platform.HealthKit.degreeCelsiusUnit
import platform.HealthKit.degreeFahrenheitUnit
import platform.HealthKit.predicateForSamplesWithStartDate
import platform.HealthKit.preferredUnitsForQuantityTypes
import kotlin.coroutines.resumeWithException

internal class HealthKitManager : HealthManager {

    private val healthKit by lazy { HKHealthStore() }

    override fun isAvailable(): Result<Boolean> = runCatching {
        HKHealthStore.isHealthDataAvailable()
    }

    override suspend fun isAuthorized(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        healthKit.getRequestStatusForAuthorizationToShareTypes(
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
        healthKit.requestAuthorizationToShareTypes(
            typesToShare = writeTypes.map { it.toHKSampleType() }.flatten().filterNotNull().toSet(),
            readTypes = readTypes.map { it.toHKSampleType() }.flatten().filterNotNull().toSet(),
        ) { _, error ->
            if (continuation.isCancelled) return@requestAuthorizationToShareTypes

            if (error != null) {
                @Suppress("RemoveExplicitTypeArguments")
                continuation.resume(Result.failure<Unit>(Throwable(error.toString())))
            } else {
                // We don't case about result here, it will be mapped to isAuthorized
                continuation.resume(Result.success(Unit))
            }
        }
    }.mapCatching {
        isAuthorized(readTypes = readTypes, writeTypes = writeTypes).getOrThrow()
    }

    override suspend fun isRevokeAuthorizationSupported(): Result<Boolean> =
        Result.success(false)

    override suspend fun revokeAuthorization(): Result<Unit> =
        Result.failure(NotImplementedError())

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
                    .onFailure { return Result.failure(it) }
                    .getOrThrow()
            }
            .flatten()

        when {
            result.isEmpty() -> {
                return@runCatching emptyList()
            }

            result.firstOrNull() is HKQuantitySample -> {
                val temperaturePreference = suspend { getTemperaturePreference() }

                @Suppress("UNCHECKED_CAST")
                (result as List<HKQuantitySample>).toHealthRecord(temperaturePreference)
            }

            result.firstOrNull() is HKCategorySample -> {
                @Suppress("UNCHECKED_CAST")
                (result as List<HKCategorySample>).toHealthRecords()
            }

            else -> {
                throw NotImplementedError("Result ${result.firstOrNull()} is not supported")
            }
        }
    }

    override suspend fun writeData(
        records: List<HealthRecord>,
    ): Result<Unit> = suspendCancellableCoroutine { continuation ->
        healthKit.saveObjects(records.mapNotNull { it.toHKObjects() }.flatten()) { _, error ->
            if (continuation.isCancelled) return@saveObjects

            if (error != null) {
                continuation.resume(Result.failure(Throwable(error.toString())))
                return@saveObjects
            }

            continuation.resume(Result.success(Unit))
        }
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

        return type.toHKQuantityType()
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
                    .onFailure { return Result.failure(it) }
                    .getOrThrow()
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
            healthKit.preferredUnitsForQuantityTypes(setOf(quantityType)) { result, error ->
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
                    @Suppress("UNCHECKED_CAST")
                    val records = result as List<HKQuantitySample>
                    continuation.resume(Result.success(records))
                }

                result.firstOrNull() is HKCategorySample -> {
                    @Suppress("UNCHECKED_CAST")
                    val records = result as List<HKCategorySample>
                    continuation.resume(Result.success(records))
                }

                else -> {
                    continuation.resume(Result.failure(NotImplementedError("Result ${result.firstOrNull()} is not supported")))
                }
            }
        }

        healthKit.executeQuery(query)
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

        healthKit.executeQuery(query)
    }

}