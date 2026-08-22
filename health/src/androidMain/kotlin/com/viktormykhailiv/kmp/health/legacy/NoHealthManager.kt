package com.viktormykhailiv.kmp.health.legacy

import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthManager
import com.viktormykhailiv.kmp.health.HealthRecord
import com.viktormykhailiv.kmp.health.region.RegionalPreferences
import kotlin.time.Duration
import kotlin.time.Instant

internal class NoHealthManager : HealthManager {

    override fun isAvailable(): Result<Boolean> =
        Result.success(false)

    override suspend fun isAuthorized(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Result<Boolean> =
        notAvailable()

    override suspend fun requestAuthorization(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
        requestReadHealthDataInBackground: Boolean,
        requestReadHealthDataHistory: Boolean,
    ): Result<Boolean> =
        notAvailable()

    override suspend fun isRevokeAuthorizationSupported(): Result<Boolean> =
        notAvailable()

    override suspend fun revokeAuthorization(): Result<Unit> =
        notAvailable()

    override fun openSystemHealthSettings(): Result<Unit> =
        notAvailable()

    override suspend fun hasReadHealthDataInBackgroundPermission(): Result<Boolean> =
        notAvailable()

    override suspend fun requestReadHealthDataInBackgroundPermission(): Result<Boolean> =
        notAvailable()

    override suspend fun hasReadHealthDataHistoryPermission(): Result<Boolean> =
        notAvailable()

    override suspend fun requestReadHealthDataHistoryPermission(): Result<Boolean> =
        notAvailable()

    override suspend fun readData(
        startTime: Instant,
        endTime: Instant,
        type: HealthDataType,
    ): Result<List<HealthRecord>> =
        notAvailable()

    override suspend fun writeData(records: List<HealthRecord>): Result<Unit> =
        notAvailable()

    override suspend fun aggregate(
        startTime: Instant,
        endTime: Instant,
        type: HealthDataType,
    ): Result<HealthAggregatedRecord> =
        notAvailable()

    override suspend fun aggregateGroupByDuration(
        startTime: Instant,
        endTime: Instant,
        sliceWidth: Duration,
        type: HealthDataType,
    ): Result<List<HealthAggregatedRecord>> =
        notAvailable()

    override suspend fun getRegionalPreferences(): Result<RegionalPreferences> =
        notAvailable()

    private fun <T> notAvailable(): Result<T> =
        Result.failure(RuntimeException("Health manager is not available on this device"))

}