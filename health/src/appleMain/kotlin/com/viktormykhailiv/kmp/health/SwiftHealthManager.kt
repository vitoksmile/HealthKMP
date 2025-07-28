@file:Suppress("unused")

package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.region.RegionalPreferences
import kotlinx.datetime.toKotlinInstant
import platform.Foundation.NSDate

/**
 * Swift compatible version of [HealthManager] for better interoperability and error handling.
 *
 * `kotlin.Result` is inline class and is transformed into `Any?` which is not convenient.
 *
 * @see <a href=https://kotlinlang.org/docs/native-objc-interop.html#errors-and-exceptions>Errors and exceptions</a>
 */
class SwiftHealthManager(
    private val manager: HealthManager,
) {

    @Throws(Throwable::class)
    fun isAvailable(): Boolean {
        return manager.isAvailable().getOrThrow()
    }

    @Throws(Throwable::class)
    suspend fun isAuthorized(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Boolean {
        return manager.isAuthorized(
            readTypes = readTypes,
            writeTypes = writeTypes,
        ).getOrThrow()
    }

    @Throws(Throwable::class)
    suspend fun requestAuthorization(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Boolean {
        return manager.requestAuthorization(
            readTypes = readTypes,
            writeTypes = writeTypes,
        ).getOrThrow()
    }

    @Throws(Throwable::class)
    suspend fun isRevokeAuthorizationSupported(): Boolean {
        return manager.isRevokeAuthorizationSupported().getOrThrow()
    }

    @Throws(Throwable::class)
    suspend fun revokeAuthorization() {
        manager.revokeAuthorization().getOrThrow()
    }

    @Throws(Throwable::class)
    suspend fun readData(
        startTime: NSDate,
        endTime: NSDate,
        type: HealthDataType,
    ): List<HealthRecord> {
        return manager.readData(
            startTime = startTime.toKotlinInstant(),
            endTime = endTime.toKotlinInstant(),
            type = type,
        ).getOrThrow()
    }

    @Throws(Throwable::class)
    suspend fun writeData(
        records: List<HealthRecord>,
    ) {
        manager.writeData(
            records = records,
        ).getOrThrow()
    }

    @Throws(Throwable::class)
    suspend fun aggregate(
        startTime: NSDate,
        endTime: NSDate,
        type: HealthDataType,
    ): HealthAggregatedRecord {
        return manager.aggregate(
            startTime = startTime.toKotlinInstant(),
            endTime = endTime.toKotlinInstant(),
            type = type,
        ).getOrThrow()
    }

    @Throws(Throwable::class)
    suspend fun getRegionalPreferences(): RegionalPreferences {
        return manager.getRegionalPreferences().getOrThrow()
    }

}
