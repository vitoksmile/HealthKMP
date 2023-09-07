@file:Suppress("ClassName")

package com.vitoksmile.kmp.health

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.Instant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSSortDescriptor
import platform.HealthKit.HKAuthorizationRequestStatusUnnecessary
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKObjectQueryNoLimit
import platform.HealthKit.HKQuantitySample
import platform.HealthKit.HKQuery
import platform.HealthKit.HKQueryOptionStrictStartDate
import platform.HealthKit.HKSampleQuery
import platform.HealthKit.HKSampleSortIdentifierEndDate
import platform.HealthKit.predicateForSamplesWithStartDate

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
            typesToShare = writeTypes.mapNotNull { it.toHKSampleType() }.toSet(),
            readTypes = readTypes.mapNotNull { it.toHKSampleType() }.toSet(),
        ) { status, error ->
            if (continuation.isCancelled) return@getRequestStatusForAuthorizationToShareTypes

            if (error != null) {
                continuation.resumeWithException(Throwable(error.toString()))
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
            typesToShare = writeTypes.mapNotNull { it.toHKSampleType() }.toSet(),
            readTypes = readTypes.mapNotNull { it.toHKSampleType() }.toSet(),
        ) { _, error ->
            if (continuation.isCancelled) return@requestAuthorizationToShareTypes

            if (error != null) {
                continuation.resumeWithException(Throwable(error.toString()))
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
    ): Result<List<HealthRecord>> = suspendCancellableCoroutine { continuation ->
        val query = HKSampleQuery(
            sampleType = type.toHKSampleType()
                ?: run {
                    continuation.resume(Result.failure(NotImplementedError("Data type $type is not supported")))
                    return@suspendCancellableCoroutine
                },
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
                continuation.resumeWithException(Throwable(error.toString()))
                return@HKSampleQuery
            }

            when {
                result == null || result.isEmpty() -> {
                    continuation.resume(Result.success(emptyList()))
                }

                result.firstOrNull() is HKQuantitySample -> {
                    @Suppress("UNCHECKED_CAST")
                    val records = (result as List<HKQuantitySample>)
                        .mapNotNull { it.toHealthRecord() }
                    continuation.resume(Result.success(records))
                }

                else -> {
                    continuation.resume(Result.failure(NotImplementedError("Result ${result.firstOrNull()} is not supported")))
                }
            }
        }

        healthKit.executeQuery(query)
    }

    override suspend fun writeData(
        records: List<HealthRecord>,
    ): Result<Unit> = suspendCancellableCoroutine { continuation ->
        healthKit.saveObjects(records.mapNotNull { it.toHKQuantitySample() }) { _, error ->
            if (continuation.isCancelled) return@saveObjects

            if (error != null) {
                continuation.resumeWithException(Throwable(error.toString()))
                return@saveObjects
            }

            continuation.resume(Result.success(Unit))
        }
    }
}