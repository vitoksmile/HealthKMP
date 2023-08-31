package com.vitoksmile.kmp.health

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
}