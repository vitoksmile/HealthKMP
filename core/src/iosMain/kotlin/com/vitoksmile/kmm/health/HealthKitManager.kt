@file:Suppress("ClassName")

package com.vitoksmile.kmm.health

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.HealthKit.HKAuthorizationRequestStatusUnnecessary
import platform.HealthKit.HKHealthStore

class HealthKitManager : HealthManager {

    private val healthKit by lazy { HKHealthStore() }

    override fun isAvailable(): Result<Boolean> = runCatching {
        HKHealthStore.isHealthDataAvailable()
    }

    override suspend fun isAuthorized(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>
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

    override suspend fun isRevokeAuthorizationSupported(): Result<Boolean> = Result.success(false)

    override suspend fun revokeAuthorization(): Result<Unit> = Result.failure(NotImplementedError())
}