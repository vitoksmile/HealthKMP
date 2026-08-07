package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.region.RegionalPreferences
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Core interface for interacting with health data.
 *
 * Provides methods for checking availability, requesting authorization, reading, writing,
 * and aggregating health data.
 */
interface HealthManager {

    /**
     * Checks if health data is available on the current device.
     *
     * @return A [Result] containing true if available, false otherwise.
     */
    fun isAvailable(): Result<Boolean>

    /**
     * Checks if the specified health data types are authorized for reading and writing.
     *
     * @param readTypes The list of [HealthDataType] to check for read authorization.
     * @param writeTypes The list of [HealthDataType] to check for write authorization.
     * @return A [Result] containing true if all types are authorized, false otherwise.
     */
    suspend fun isAuthorized(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Result<Boolean>

    /**
     * Requests authorization from the user to read and write the specified health data types.
     *
     * @param readTypes The list of [HealthDataType] to request read authorization for.
     * @param writeTypes The list of [HealthDataType] to request write authorization for.
     * @param requestReadHealthDataInBackground Whether to also request permission to read health data while the app is in the background.
     * @return A [Result] containing true if authorization was granted for the requested types, false otherwise.
     */
    suspend fun requestAuthorization(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
        requestReadHealthDataInBackground: Boolean = false,
    ): Result<Boolean>

    /**
     * Checks if revoking authorization is supported on the current platform.
     *
     * @return A [Result] containing true if supported, false otherwise.
     */
    suspend fun isRevokeAuthorizationSupported(): Result<Boolean>

    /**
     * Revokes all previously granted authorizations.
     *
     * @return A [Result] containing [Unit] if successful.
     */
    suspend fun revokeAuthorization(): Result<Unit>

    /**
     * Checks if the app has permission to read health data while running in the background.
     *
     * @return A [Result] containing true if background read permission is granted, false otherwise.
     */
    suspend fun hasReadHealthDataInBackgroundPermission(): Result<Boolean>

    /**
     * Requests authorization from the user to read health data while the app is in the background.
     *
     * On some platforms (like Android with Health Connect), this may require a separate
     * permission request from the standard authorization.
     *
     * @return A [Result] containing true if background read authorization was granted, false otherwise.
     */
    suspend fun requestReadHealthDataInBackgroundPermission(): Result<Boolean>

    /**
     * Reads health data records of the specified type within the given time range.
     *
     * @param startTime The start time of the range (inclusive).
     * @param endTime The end time of the range (exclusive).
     * @param type The [HealthDataType] to read.
     * @return A [Result] containing a list of [HealthRecord]s.
     */
    suspend fun readData(
        startTime: Instant,
        endTime: Instant,
        type: HealthDataType,
    ): Result<List<HealthRecord>>

    /**
     * Writes health data records.
     *
     * @param records The list of [HealthRecord]s to write.
     * @return A [Result] containing [Unit] if successful.
     */
    suspend fun writeData(
        records: List<HealthRecord>,
    ): Result<Unit>

    /**
     * Calculates aggregation for [HealthDataType] within the specified time range.
     *
     * @param startTime The start time of the range (inclusive).
     * @param endTime The end time of the range (exclusive).
     * @param type The [HealthDataType] to aggregate.
     * @return A [Result] containing a single [HealthAggregatedRecord] for the entire time range.
     */
    suspend fun aggregate(
        startTime: Instant,
        endTime: Instant,
        type: HealthDataType,
    ): Result<HealthAggregatedRecord>

    /**
     * Calculates aggregation for [HealthDataType] within the specified time range,
     * grouped into equal-duration slices.
     *
     * Note: On Android (Health Connect), metrics that require custom aggregation (such as
     * [BloodGlucose], [BodyFat], [BodyTemperature], and [LeanBodyMass]) do not support native
     * duration slicing and will return a single aggregate record spanning [startTime, endTime).
     *
     * @param startTime The start time of the range (inclusive).
     * @param endTime The end time of the range (exclusive).
     * @param sliceWidth The duration of each time slice (bucket) within [startTime, endTime).
     * @param type The [HealthDataType] to aggregate.
     * @return A [Result] containing a list of [HealthAggregatedRecord]s for each time slice.
     */
    suspend fun aggregateGroupByDuration(
        startTime: Instant,
        endTime: Instant,
        sliceWidth: Duration,
        type: HealthDataType,
    ): Result<List<HealthAggregatedRecord>>

    /**
     * Retrieves the user's regional preferences (e.g., units).
     *
     * @return A [Result] containing [RegionalPreferences].
     */
    suspend fun getRegionalPreferences(): Result<RegionalPreferences>

}