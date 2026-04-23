package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.region.RegionalPreferences
import kotlin.time.Instant

internal class NoHealthManager: HealthManager {

    internal class HealthManagerUnavailableException: RuntimeException(
        "Health manager is not available on this device"
    )

    private val unavailableException by lazy {
        HealthManagerUnavailableException()
    }

    override fun isAvailable(): Result<Boolean> {
        return Result.success(false)
    }

    override suspend fun isAuthorized(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Result<Boolean> {
        return Result.failure(unavailableException)
    }

    override suspend fun requestAuthorization(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Result<Boolean> {
        return Result.failure(unavailableException)
    }

    override suspend fun isRevokeAuthorizationSupported(): Result<Boolean> {
        return Result.failure(unavailableException)
    }

    override suspend fun revokeAuthorization(): Result<Unit> {
        return Result.failure(unavailableException)
    }

    override suspend fun readData(
        startTime: Instant,
        endTime: Instant,
        type: HealthDataType,
    ): Result<List<HealthRecord>> {
        return Result.failure(unavailableException)
    }

    override suspend fun writeData(records: List<HealthRecord>): Result<Unit> {
        return Result.failure(unavailableException)
    }

    override suspend fun aggregate(
        startTime: Instant,
        endTime: Instant,
        type: HealthDataType,
    ): Result<HealthAggregatedRecord> {
        return Result.failure(unavailableException)
    }

    override suspend fun getRegionalPreferences(): Result<RegionalPreferences> {
        return Result.failure(unavailableException)
    }
}