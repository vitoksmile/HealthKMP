@file:Suppress("unused")

package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.aggregate.ActiveEnergyBurnedAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.DistanceAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.StepsAggregatedRecord
import com.viktormykhailiv.kmp.health.records.ActiveEnergyBurnedRecord
import com.viktormykhailiv.kmp.health.records.DistanceRecord
import com.viktormykhailiv.kmp.health.records.ExerciseSessionRecord
import com.viktormykhailiv.kmp.health.records.StepsRecord
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
    fun openSystemHealthSettings() {
        manager.openSystemHealthSettings().getOrThrow()
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
    suspend fun readActiveEnergyBurned(
        startTime: NSDate,
        endTime: NSDate,
    ): List<ActiveEnergyBurnedRecord> {
        return readData(
            startTime = startTime,
            endTime = endTime,
            type = HealthDataType.ActiveEnergyBurned,
        ).filterIsInstance<ActiveEnergyBurnedRecord>()
    }

    @Throws(Throwable::class)
    suspend fun aggregateActiveEnergyBurned(
        startTime: NSDate,
        endTime: NSDate,
    ): ActiveEnergyBurnedAggregatedRecord {
        return aggregate(
            startTime = startTime,
            endTime = endTime,
            type = HealthDataType.ActiveEnergyBurned,
        ) as ActiveEnergyBurnedAggregatedRecord
    }

    @Throws(Throwable::class)
    suspend fun readDistance(
        startTime: NSDate,
        endTime: NSDate,
    ): List<DistanceRecord> {
        return readData(
            startTime = startTime,
            endTime = endTime,
            type = HealthDataType.Distance,
        ).filterIsInstance<DistanceRecord>()
    }

    @Throws(Throwable::class)
    suspend fun aggregateDistance(
        startTime: NSDate,
        endTime: NSDate,
    ): DistanceAggregatedRecord {
        return aggregate(
            startTime = startTime,
            endTime = endTime,
            type = HealthDataType.Distance,
        ) as DistanceAggregatedRecord
    }

    @Throws(Throwable::class)
    suspend fun readExercise(
        startTime: NSDate,
        endTime: NSDate,
    ): List<ExerciseSessionRecord> {
        return readData(
            startTime = startTime,
            endTime = endTime,
            type = HealthDataType.Exercise(),
        ).filterIsInstance<ExerciseSessionRecord>()
    }

    @Throws(Throwable::class)
    suspend fun readSteps(
        startTime: NSDate,
        endTime: NSDate,
    ): List<StepsRecord> {
        return readData(
            startTime = startTime,
            endTime = endTime,
            type = HealthDataType.Steps,
        ).filterIsInstance<StepsRecord>()
    }

    @Throws(Throwable::class)
    suspend fun aggregateSteps(
        startTime: NSDate,
        endTime: NSDate,
    ): StepsAggregatedRecord {
        return aggregate(
            startTime = startTime,
            endTime = endTime,
            type = HealthDataType.Steps,
        ) as StepsAggregatedRecord
    }

    @Throws(Throwable::class)
    suspend fun getRegionalPreferences(): RegionalPreferences {
        return manager.getRegionalPreferences().getOrThrow()
    }

}
