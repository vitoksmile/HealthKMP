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
import com.viktormykhailiv.kmp.health.HealthDataType.CyclingPedalingCadence
import com.viktormykhailiv.kmp.health.HealthDataType.Power
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
import com.viktormykhailiv.kmp.health.aggregate.CyclingPedalingCadenceAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.PowerAggregatedRecord
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
import com.viktormykhailiv.kmp.health.records.CyclingPedalingCadenceRecord
import com.viktormykhailiv.kmp.health.records.PowerRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.records.metadata.Device
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.records.metadata.getLocalDevice
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun generateManualEntryMetadata(): Metadata = Metadata.manualEntry(
    id = Uuid.random().toString(),
    device = Device.getLocalDevice(),
)

val IntervalRecord.duration: Duration
    get() = endTime - startTime

// region Read extensions
suspend fun HealthManager.readBloodGlucose(
    startTime: Instant,
    endTime: Instant,
): Result<List<BloodGlucoseRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = BloodGlucose,
    ).map { it.filterIsInstance<BloodGlucoseRecord>() }

suspend fun HealthManager.readBloodPressure(
    startTime: Instant,
    endTime: Instant,
): Result<List<BloodPressureRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = BloodPressure,
    ).map { it.filterIsInstance<BloodPressureRecord>() }

suspend fun HealthManager.readBodyFat(
    startTime: Instant,
    endTime: Instant,
): Result<List<BodyFatRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = BodyFat,
    ).map { it.filterIsInstance<BodyFatRecord>() }

suspend fun HealthManager.readBodyTemperature(
    startTime: Instant,
    endTime: Instant,
): Result<List<BodyTemperatureRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = BodyTemperature,
    ).map { it.filterIsInstance<BodyTemperatureRecord>() }

suspend fun HealthManager.readCyclingPedalingCadence(
    startTime: Instant,
    endTime: Instant,
): Result<List<CyclingPedalingCadenceRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = CyclingPedalingCadence,
    ).map { it.filterIsInstance<CyclingPedalingCadenceRecord>() }

suspend fun HealthManager.readExercise(
    startTime: Instant,
    endTime: Instant,
    exercise: Exercise = Exercise(),
): Result<List<ExerciseSessionRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = exercise,
    ).map { it.filterIsInstance<ExerciseSessionRecord>() }

suspend fun HealthManager.readHeartRate(
    startTime: Instant,
    endTime: Instant,
): Result<List<HeartRateRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = HeartRate,
    ).map { it.filterIsInstance<HeartRateRecord>() }

suspend fun HealthManager.readHeight(
    startTime: Instant,
    endTime: Instant,
): Result<List<HeightRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = Height,
    ).map { it.filterIsInstance<HeightRecord>() }

suspend fun HealthManager.readLeanBodyMass(
    startTime: Instant,
    endTime: Instant,
): Result<List<LeanBodyMassRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = LeanBodyMass,
    ).map { it.filterIsInstance<LeanBodyMassRecord>() }

suspend fun HealthManager.readPower(
    startTime: Instant,
    endTime: Instant,
): Result<List<PowerRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = Power,
    ).map { it.filterIsInstance<PowerRecord>() }

suspend fun HealthManager.readSleep(
    startTime: Instant,
    endTime: Instant,
): Result<List<SleepSessionRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = Sleep,
    ).map { it.filterIsInstance<SleepSessionRecord>() }

suspend fun HealthManager.readSteps(
    startTime: Instant,
    endTime: Instant,
): Result<List<StepsRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = Steps,
    ).map { it.filterIsInstance<StepsRecord>() }

suspend fun HealthManager.readWeight(
    startTime: Instant,
    endTime: Instant,
): Result<List<WeightRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = Weight,
    ).map { it.filterIsInstance<WeightRecord>() }
// endregion

// region Aggregate extensions
suspend fun HealthManager.aggregateBloodGlucose(
    startTime: Instant,
    endTime: Instant,
): Result<BloodGlucoseAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = BloodGlucose,
    ).mapCatching { it as BloodGlucoseAggregatedRecord }

suspend fun HealthManager.aggregateBloodPressure(
    startTime: Instant,
    endTime: Instant,
): Result<BloodPressureAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = BloodPressure,
    ).mapCatching { it as BloodPressureAggregatedRecord }

suspend fun HealthManager.aggregateBodyFat(
    startTime: Instant,
    endTime: Instant,
): Result<BodyFatAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = BodyFat,
    ).mapCatching { it as BodyFatAggregatedRecord }

suspend fun HealthManager.aggregateBodyTemperature(
    startTime: Instant,
    endTime: Instant,
): Result<BodyTemperatureAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = BodyTemperature,
    ).mapCatching { it as BodyTemperatureAggregatedRecord }

suspend fun HealthManager.aggregateCyclingPedalingCadence(
    startTime: Instant,
    endTime: Instant,
): Result<CyclingPedalingCadenceAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = CyclingPedalingCadence,
    ).mapCatching { it as CyclingPedalingCadenceAggregatedRecord }

suspend fun HealthManager.aggregateHeartRate(
    startTime: Instant,
    endTime: Instant,
): Result<HeartRateAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = HeartRate,
    ).mapCatching { it as HeartRateAggregatedRecord }

suspend fun HealthManager.aggregateHeight(
    startTime: Instant,
    endTime: Instant,
): Result<HeightAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Height,
    ).mapCatching { it as HeightAggregatedRecord }

suspend fun HealthManager.aggregateLeanBodyMass(
    startTime: Instant,
    endTime: Instant,
): Result<LeanBodyMassAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = LeanBodyMass,
    ).mapCatching { it as LeanBodyMassAggregatedRecord }

suspend fun HealthManager.aggegratePower(
    startTime: Instant,
    endTime: Instant,
): Result<PowerAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Power,
    ).mapCatching { it as PowerAggregatedRecord }

suspend fun HealthManager.aggregateSleep(
    startTime: Instant,
    endTime: Instant,
): Result<SleepAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Sleep,
    ).mapCatching { it as SleepAggregatedRecord }

suspend fun HealthManager.aggregateSteps(
    startTime: Instant,
    endTime: Instant,
): Result<StepsAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Steps,
    ).mapCatching { it as StepsAggregatedRecord }

suspend fun HealthManager.aggregateWeight(
    startTime: Instant,
    endTime: Instant,
): Result<WeightAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Weight,
    ).mapCatching { it as WeightAggregatedRecord }
// endregion
