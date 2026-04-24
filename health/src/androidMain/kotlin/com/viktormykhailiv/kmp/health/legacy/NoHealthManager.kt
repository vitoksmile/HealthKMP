package com.viktormykhailiv.kmp.health.legacy

import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthManager
import com.viktormykhailiv.kmp.health.HealthRecord
import com.viktormykhailiv.kmp.health.region.RegionalPreferences
import kotlin.time.Instant

internal class NoHealthManager : HealthManager {

    internal class HealthManagerUnavailableException :
        RuntimeException("Health manager is not available on this device")

    private val unavailableException by lazy { HealthManagerUnavailableException() }

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
        requestReadHealthDataInBackground: Boolean,
    ): Result<Boolean> {
        return Result.failure(unavailableException)
    }

    override suspend fun isRevokeAuthorizationSupported(): Result<Boolean> {
        return Result.failure(unavailableException)
    }

    override suspend fun revokeAuthorization(): Result<Unit> {
        return Result.failure(unavailableException)
    }

    override suspend fun hasReadHealthDataInBackgroundPermission(): Result<Boolean> {
        return Result.failure(unavailableException)
    }

    override suspend fun requestReadHealthDataInBackgroundPermission(): Result<Boolean> {
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