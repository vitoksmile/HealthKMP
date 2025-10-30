@file:Suppress("unused")

package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.HealthDataType.BloodGlucose
import com.viktormykhailiv.kmp.health.HealthDataType.BloodPressure
import com.viktormykhailiv.kmp.health.HealthDataType.BodyFat
import com.viktormykhailiv.kmp.health.HealthDataType.BodyTemperature
import com.viktormykhailiv.kmp.health.HealthDataType.Exercise
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Height
import com.viktormykhailiv.kmp.health.HealthDataType.LeanBodyMass
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.aggregate.BloodGlucoseAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.BloodPressureAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.BodyFatAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.BodyTemperatureAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.HeartRateAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.HeightAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.LeanBodyMassAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.SleepAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.StepsAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.WeightAggregatedRecord
import com.viktormykhailiv.kmp.health.records.BloodGlucoseRecord
import com.viktormykhailiv.kmp.health.records.BloodPressureRecord
import com.viktormykhailiv.kmp.health.records.BodyFatRecord
import com.viktormykhailiv.kmp.health.records.BodyTemperatureRecord
import com.viktormykhailiv.kmp.health.records.ExerciseSessionRecord
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.HeightRecord
import com.viktormykhailiv.kmp.health.records.LeanBodyMassRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import kotlin.time.Instant
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
suspend fun SwiftHealthManager.readBloodGlucose(
    startTime: NSDate,
    endTime: NSDate,
): List<BloodGlucoseRecord> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = BloodGlucose,
    ).filterIsInstance<BloodGlucoseRecord>()

@Throws(Throwable::class)
suspend fun SwiftHealthManager.readBloodPressure(
    startTime: NSDate,
    endTime: NSDate,
): List<BloodPressureRecord> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = BloodPressure,
    ).filterIsInstance<BloodPressureRecord>()

@Throws(Throwable::class)
suspend fun SwiftHealthManager.readBodyFat(
    startTime: NSDate,
    endTime: NSDate,
): List<BodyFatRecord> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = BodyFat,
    ).filterIsInstance<BodyFatRecord>()

@Throws(Throwable::class)
suspend fun SwiftHealthManager.readBodyTemperature(
    startTime: NSDate,
    endTime: NSDate,
): List<BodyTemperatureRecord> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = BodyTemperature,
    ).filterIsInstance<BodyTemperatureRecord>()

@Throws(Throwable::class)
suspend fun SwiftHealthManager.readExercise(
    startTime: NSDate,
    endTime: NSDate,
    exercise: Exercise = Exercise(),
): List<ExerciseSessionRecord> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = exercise,
    ).filterIsInstance<ExerciseSessionRecord>()

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
suspend fun SwiftHealthManager.readHeight(
    startTime: NSDate,
    endTime: NSDate,
): List<HeightRecord> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = Height,
    ).filterIsInstance<HeightRecord>()

@Throws(Throwable::class)
suspend fun SwiftHealthManager.readLeanBodyMass(
    startTime: NSDate,
    endTime: NSDate,
): List<LeanBodyMassRecord> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = LeanBodyMass,
    ).filterIsInstance<LeanBodyMassRecord>()

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
suspend fun SwiftHealthManager.aggregateBloodGlucose(
    startTime: NSDate,
    endTime: NSDate,
): BloodGlucoseAggregatedRecord =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = BloodGlucose,
    ) as BloodGlucoseAggregatedRecord

@Throws(Throwable::class)
suspend fun SwiftHealthManager.aggregateBloodPressure(
    startTime: NSDate,
    endTime: NSDate,
): BloodPressureAggregatedRecord =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = BloodPressure,
    ) as BloodPressureAggregatedRecord

@Throws(Throwable::class)
suspend fun SwiftHealthManager.aggregateBodyFat(
    startTime: NSDate,
    endTime: NSDate,
): BodyFatAggregatedRecord =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = BodyFat,
    ) as BodyFatAggregatedRecord

@Throws(Throwable::class)
suspend fun SwiftHealthManager.aggregateBodyTemperature(
    startTime: NSDate,
    endTime: NSDate,
): BodyTemperatureAggregatedRecord =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = BodyTemperature,
    ) as BodyTemperatureAggregatedRecord

@Throws(Throwable::class)
suspend fun SwiftHealthManager.aggregateHeartRate(
    startTime: NSDate,
    endTime: NSDate,
): HeartRateAggregatedRecord =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = HeartRate,
    ) as HeartRateAggregatedRecord

@Throws(Throwable::class)
suspend fun SwiftHealthManager.aggregateHeight(
    startTime: NSDate,
    endTime: NSDate,
): HeightAggregatedRecord =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Height,
    ) as HeightAggregatedRecord

@Throws(Throwable::class)
suspend fun SwiftHealthManager.aggregateLeanBodyMass(
    startTime: NSDate,
    endTime: NSDate,
): LeanBodyMassAggregatedRecord =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = LeanBodyMass,
    ) as LeanBodyMassAggregatedRecord

@Throws(Throwable::class)
suspend fun SwiftHealthManager.aggregateSleep(
    startTime: NSDate,
    endTime: NSDate,
): SleepAggregatedRecord =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Sleep,
    ) as SleepAggregatedRecord

@Throws(Throwable::class)
suspend fun SwiftHealthManager.aggregateSteps(
    startTime: NSDate,
    endTime: NSDate,
): StepsAggregatedRecord =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Steps,
    ) as StepsAggregatedRecord

@Throws(Throwable::class)
suspend fun SwiftHealthManager.aggregateWeight(
    startTime: NSDate,
    endTime: NSDate,
): WeightAggregatedRecord =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Weight,
    ) as WeightAggregatedRecord
// endregion
