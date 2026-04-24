package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.region.RegionalPreferences
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
     * @return A [Result] containing true if authorization was granted, false otherwise.
     */
    suspend fun requestAuthorization(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
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
     * Aggregates health data of the specified type within the given time range.
     *
     * @param startTime The start time of the range (inclusive).
     * @param endTime The end time of the range (exclusive).
     * @param type The [HealthDataType] to aggregate.
     * @return A [Result] containing a [HealthAggregatedRecord].
     */
    suspend fun aggregate(
        startTime: Instant,
        endTime: Instant,
        type: HealthDataType,
    ): Result<HealthAggregatedRecord>

    /**
     * Retrieves the user's regional preferences (e.g., units).
     *
     * @return A [Result] containing [RegionalPreferences].
     */
    suspend fun getRegionalPreferences(): Result<RegionalPreferences>

}