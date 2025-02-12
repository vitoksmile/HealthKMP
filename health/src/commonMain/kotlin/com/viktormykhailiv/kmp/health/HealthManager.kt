package com.viktormykhailiv.kmp.health

import kotlinx.datetime.Instant

interface HealthManager {

    fun isAvailable(): Result<Boolean>

    suspend fun isAuthorized(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Result<Boolean>

    suspend fun requestAuthorization(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Result<Boolean>

    suspend fun isRevokeAuthorizationSupported(): Result<Boolean>

    suspend fun revokeAuthorization(): Result<Unit>

    suspend fun readData(
        startTime: Instant,
        endTime: Instant,
        type: HealthDataType,
    ): Result<List<HealthRecord>>

    suspend fun writeData(
        records: List<HealthRecord>,
    ): Result<Unit>
}