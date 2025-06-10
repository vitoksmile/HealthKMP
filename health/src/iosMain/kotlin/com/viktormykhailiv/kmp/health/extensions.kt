package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.aggregate.HeartRateAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.SleepAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.StepsAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.WeightAggregatedRecord
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import kotlinx.datetime.Instant
import kotlinx.datetime.toNSDate
import kotlinx.datetime.toKotlinInstant
import platform.Foundation.NSDate

/**
 * Converts the [Instant] to an instance of [NSDate].
 */
fun Instant.toNSDate(): NSDate = toNSDate()

/**
 * Converts the [NSDate] to the corresponding [Instant].
 */
fun NSDate.toKotlinInstant(): Instant = toKotlinInstant()

// region Read extensions
@Throws(Throwable::class)
suspend fun SwiftHealthManager.readHeartRage(
    startTime: NSDate,
    endTime: NSDate,
): List<HeartRateRecord> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = HeartRate,
    ).filterIsInstance<HeartRateRecord>()

@Throws(Throwable::class)
suspend fun SwiftHealthManager.readSleep(
    startTime: NSDate,
    endTime: NSDate,
): List<SleepSessionRecord> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = Sleep,
    ).filterIsInstance<SleepSessionRecord>()

@Throws(Throwable::class)
suspend fun SwiftHealthManager.readSteps(
    startTime: NSDate,
    endTime: NSDate,
): List<StepsRecord> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = Steps,
    ).filterIsInstance<StepsRecord>()

@Throws(Throwable::class)
suspend fun SwiftHealthManager.readWeight(
    startTime: NSDate,
    endTime: NSDate,
): List<WeightRecord> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = Weight,
    ).filterIsInstance<WeightRecord>()
// endregion

// region Aggregate extensions
@Throws(Throwable::class)
suspend fun SwiftHealthManager.aggregateHeartRate(
    startTime: NSDate,
    endTime: NSDate,
): HeartRateAggregatedRecord {
    return aggregate(
        startTime = startTime,
        endTime = endTime,
        type = HeartRate,
    ) as HeartRateAggregatedRecord
}

@Throws(Throwable::class)
suspend fun SwiftHealthManager.aggregateSleep(
    startTime: NSDate,
    endTime: NSDate,
): SleepAggregatedRecord {
    return aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Sleep,
    ) as SleepAggregatedRecord
}

@Throws(Throwable::class)
suspend fun SwiftHealthManager.aggregateSteps(
    startTime: NSDate,
    endTime: NSDate,
): StepsAggregatedRecord {
    return aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Steps,
    ) as StepsAggregatedRecord
}

@Throws(Throwable::class)
suspend fun SwiftHealthManager.aggregateWeight(
    startTime: NSDate,
    endTime: NSDate,
): WeightAggregatedRecord {
    return aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Weight,
    ) as WeightAggregatedRecord
}
// endregion
