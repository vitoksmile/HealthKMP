@file:Suppress("unused")

package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.HealthDataType.BloodGlucose
import com.viktormykhailiv.kmp.health.HealthDataType.Distance
import com.viktormykhailiv.kmp.health.HealthDataType.ActiveEnergyBurned
import com.viktormykhailiv.kmp.health.HealthDataType.BloodPressure
import com.viktormykhailiv.kmp.health.HealthDataType.BodyFat
import com.viktormykhailiv.kmp.health.HealthDataType.BodyTemperature
import com.viktormykhailiv.kmp.health.HealthDataType.CyclingPedalingCadence
import com.viktormykhailiv.kmp.health.HealthDataType.Exercise
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Height
import com.viktormykhailiv.kmp.health.HealthDataType.LeanBodyMass
import com.viktormykhailiv.kmp.health.HealthDataType.MenstruationFlow
import com.viktormykhailiv.kmp.health.HealthDataType.MenstruationPeriod
import com.viktormykhailiv.kmp.health.HealthDataType.OvulationTest
import com.viktormykhailiv.kmp.health.HealthDataType.Power
import com.viktormykhailiv.kmp.health.HealthDataType.SexualActivity
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
import com.viktormykhailiv.kmp.health.aggregate.DistanceAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.ActiveEnergyBurnedAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.PowerAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.SleepAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.StepsAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.WeightAggregatedRecord
import com.viktormykhailiv.kmp.health.records.BloodGlucoseRecord
import com.viktormykhailiv.kmp.health.records.BloodPressureRecord
import com.viktormykhailiv.kmp.health.records.BodyFatRecord
import com.viktormykhailiv.kmp.health.records.BodyTemperatureRecord
import com.viktormykhailiv.kmp.health.records.CyclingPedalingCadenceRecord
import com.viktormykhailiv.kmp.health.records.DistanceRecord
import com.viktormykhailiv.kmp.health.records.ActiveEnergyBurnedRecord
import com.viktormykhailiv.kmp.health.records.ExerciseSessionRecord
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.HeightRecord
import com.viktormykhailiv.kmp.health.records.LeanBodyMassRecord
import com.viktormykhailiv.kmp.health.records.MenstruationFlowRecord
import com.viktormykhailiv.kmp.health.records.MenstruationPeriodRecord
import com.viktormykhailiv.kmp.health.records.OvulationTestRecord
import com.viktormykhailiv.kmp.health.records.PowerRecord
import com.viktormykhailiv.kmp.health.records.SexualActivityRecord
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

/**
 * Generates [Metadata] for a manual entry with a random UUID and the local device information.
 */
@OptIn(ExperimentalUuidApi::class)
fun generateManualEntryMetadata(): Metadata = Metadata.manualEntry(
    id = Uuid.random().toString(),
    device = Device.getLocalDevice(),
)

/**
 * Returns the duration of the [IntervalRecord].
 */
val IntervalRecord.duration: Duration
    get() = endTime - startTime

// region Read extensions
/**
 * Reads [ActiveEnergyBurnedRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [ActiveEnergyBurnedRecord]s.
 */
suspend fun HealthManager.readActiveEnergyBurned(
    startTime: Instant,
    endTime: Instant,
): Result<List<ActiveEnergyBurnedRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = ActiveEnergyBurned,
    ).map { it.filterIsInstance<ActiveEnergyBurnedRecord>() }

/**
 * Reads [BloodGlucoseRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [BloodGlucoseRecord]s.
 */
suspend fun HealthManager.readBloodGlucose(
    startTime: Instant,
    endTime: Instant,
): Result<List<BloodGlucoseRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = BloodGlucose,
    ).map { it.filterIsInstance<BloodGlucoseRecord>() }

/**
 * Reads [BloodPressureRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [BloodPressureRecord]s.
 */
suspend fun HealthManager.readBloodPressure(
    startTime: Instant,
    endTime: Instant,
): Result<List<BloodPressureRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = BloodPressure,
    ).map { it.filterIsInstance<BloodPressureRecord>() }

/**
 * Reads [BodyFatRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [BodyFatRecord]s.
 */
suspend fun HealthManager.readBodyFat(
    startTime: Instant,
    endTime: Instant,
): Result<List<BodyFatRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = BodyFat,
    ).map { it.filterIsInstance<BodyFatRecord>() }

/**
 * Reads [BodyTemperatureRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [BodyTemperatureRecord]s.
 */
suspend fun HealthManager.readBodyTemperature(
    startTime: Instant,
    endTime: Instant,
): Result<List<BodyTemperatureRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = BodyTemperature,
    ).map { it.filterIsInstance<BodyTemperatureRecord>() }

/**
 * Reads [CyclingPedalingCadenceRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [CyclingPedalingCadenceRecord]s.
 */
suspend fun HealthManager.readCyclingPedalingCadence(
    startTime: Instant,
    endTime: Instant,
): Result<List<CyclingPedalingCadenceRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = CyclingPedalingCadence,
    ).map { it.filterIsInstance<CyclingPedalingCadenceRecord>() }

/**
 * Reads [DistanceRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [DistanceRecord]s.
 */
suspend fun HealthManager.readDistance(
    startTime: Instant,
    endTime: Instant,
): Result<List<DistanceRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = Distance,
    ).map { it.filterIsInstance<DistanceRecord>() }

/**
 * Reads [ExerciseSessionRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @param exercise The [Exercise] data type configuration.
 * @return A [Result] containing a list of [ExerciseSessionRecord]s.
 */
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

/**
 * Reads [HeartRateRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [HeartRateRecord]s.
 */
suspend fun HealthManager.readHeartRate(
    startTime: Instant,
    endTime: Instant,
): Result<List<HeartRateRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = HeartRate,
    ).map { it.filterIsInstance<HeartRateRecord>() }

/**
 * Reads [HeightRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [HeightRecord]s.
 */
suspend fun HealthManager.readHeight(
    startTime: Instant,
    endTime: Instant,
): Result<List<HeightRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = Height,
    ).map { it.filterIsInstance<HeightRecord>() }

/**
 * Reads [LeanBodyMassRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [LeanBodyMassRecord]s.
 */
suspend fun HealthManager.readLeanBodyMass(
    startTime: Instant,
    endTime: Instant,
): Result<List<LeanBodyMassRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = LeanBodyMass,
    ).map { it.filterIsInstance<LeanBodyMassRecord>() }

/**
 * Reads [MenstruationFlowRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [MenstruationFlowRecord]s.
 */
suspend fun HealthManager.readMenstruationFlow(
    startTime: Instant,
    endTime: Instant,
): Result<List<MenstruationFlowRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = MenstruationFlow,
    ).map { it.filterIsInstance<MenstruationFlowRecord>() }

/**
 * Reads [MenstruationPeriodRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [MenstruationPeriodRecord]s.
 */
suspend fun HealthManager.readMenstruationPeriod(
    startTime: Instant,
    endTime: Instant,
): Result<List<MenstruationPeriodRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = MenstruationPeriod,
    ).map { it.filterIsInstance<MenstruationPeriodRecord>() }

/**
 * Reads [OvulationTestRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [OvulationTestRecord]s.
 */
suspend fun HealthManager.readOvulationTest(
    startTime: Instant,
    endTime: Instant,
): Result<List<OvulationTestRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = OvulationTest,
    ).map { it.filterIsInstance<OvulationTestRecord>() }

/**
 * Reads [PowerRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [PowerRecord]s.
 */
suspend fun HealthManager.readPower(
    startTime: Instant,
    endTime: Instant,
): Result<List<PowerRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = Power,
    ).map { it.filterIsInstance<PowerRecord>() }

/**
 * Reads [SexualActivityRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [SexualActivityRecord]s.
 */
suspend fun HealthManager.readSexualActivity(
    startTime: Instant,
    endTime: Instant,
): Result<List<SexualActivityRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = SexualActivity,
    ).map { it.filterIsInstance<SexualActivityRecord>() }

/**
 * Reads [SleepSessionRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [SleepSessionRecord]s.
 */
suspend fun HealthManager.readSleep(
    startTime: Instant,
    endTime: Instant,
): Result<List<SleepSessionRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = Sleep,
    ).map { it.filterIsInstance<SleepSessionRecord>() }

/**
 * Reads [StepsRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [StepsRecord]s.
 */
suspend fun HealthManager.readSteps(
    startTime: Instant,
    endTime: Instant,
): Result<List<StepsRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = Steps,
    ).map { it.filterIsInstance<StepsRecord>() }

/**
 * Reads [WeightRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a list of [WeightRecord]s.
 */
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
/**
 * Aggregates [ActiveEnergyBurnedRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a [ActiveEnergyBurnedAggregatedRecord].
 */
suspend fun HealthManager.aggregateActiveEnergyBurned(
    startTime: Instant,
    endTime: Instant,
): Result<ActiveEnergyBurnedAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = ActiveEnergyBurned,
    ).map { it as ActiveEnergyBurnedAggregatedRecord }

/**
 * Aggregates [BloodGlucoseRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a [BloodGlucoseAggregatedRecord].
 */
suspend fun HealthManager.aggregateBloodGlucose(
    startTime: Instant,
    endTime: Instant,
): Result<BloodGlucoseAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = BloodGlucose,
    ).map { it as BloodGlucoseAggregatedRecord }

/**
 * Aggregates [BloodPressureRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a [BloodPressureAggregatedRecord].
 */
suspend fun HealthManager.aggregateBloodPressure(
    startTime: Instant,
    endTime: Instant,
): Result<BloodPressureAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = BloodPressure,
    ).mapCatching { it as BloodPressureAggregatedRecord }

/**
 * Aggregates [BodyFatRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a [BodyFatAggregatedRecord].
 */
suspend fun HealthManager.aggregateBodyFat(
    startTime: Instant,
    endTime: Instant,
): Result<BodyFatAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = BodyFat,
    ).mapCatching { it as BodyFatAggregatedRecord }

/**
 * Aggregates [BodyTemperatureRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a [BodyTemperatureAggregatedRecord].
 */
suspend fun HealthManager.aggregateBodyTemperature(
    startTime: Instant,
    endTime: Instant,
): Result<BodyTemperatureAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = BodyTemperature,
    ).mapCatching { it as BodyTemperatureAggregatedRecord }

/**
 * Aggregates [CyclingPedalingCadenceRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a [CyclingPedalingCadenceAggregatedRecord].
 */
suspend fun HealthManager.aggregateCyclingPedalingCadence(
    startTime: Instant,
    endTime: Instant,
): Result<CyclingPedalingCadenceAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = CyclingPedalingCadence,
    ).mapCatching { it as CyclingPedalingCadenceAggregatedRecord }

/**
 * Aggregates [DistanceRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a [DistanceAggregatedRecord].
 */
suspend fun HealthManager.aggregateDistance(
    startTime: Instant,
    endTime: Instant,
): Result<DistanceAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Distance,
    ).map { it as DistanceAggregatedRecord }

/**
 * Aggregates [HeartRateRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a [HeartRateAggregatedRecord].
 */
suspend fun HealthManager.aggregateHeartRate(
    startTime: Instant,
    endTime: Instant,
): Result<HeartRateAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = HeartRate,
    ).mapCatching { it as HeartRateAggregatedRecord }

/**
 * Aggregates [HeightRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a [HeightAggregatedRecord].
 */
suspend fun HealthManager.aggregateHeight(
    startTime: Instant,
    endTime: Instant,
): Result<HeightAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Height,
    ).mapCatching { it as HeightAggregatedRecord }

/**
 * Aggregates [LeanBodyMassRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a [LeanBodyMassAggregatedRecord].
 */
suspend fun HealthManager.aggregateLeanBodyMass(
    startTime: Instant,
    endTime: Instant,
): Result<LeanBodyMassAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = LeanBodyMass,
    ).mapCatching { it as LeanBodyMassAggregatedRecord }

/**
 * Aggregates [PowerRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a [PowerAggregatedRecord].
 */
suspend fun HealthManager.aggregatePower(
    startTime: Instant,
    endTime: Instant,
): Result<PowerAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Power,
    ).mapCatching { it as PowerAggregatedRecord }

/**
 * Aggregates [SleepSessionRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a [SleepAggregatedRecord].
 */
suspend fun HealthManager.aggregateSleep(
    startTime: Instant,
    endTime: Instant,
): Result<SleepAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Sleep,
    ).mapCatching { it as SleepAggregatedRecord }

/**
 * Aggregates [StepsRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a [StepsAggregatedRecord].
 */
suspend fun HealthManager.aggregateSteps(
    startTime: Instant,
    endTime: Instant,
): Result<StepsAggregatedRecord> =
    aggregate(
        startTime = startTime,
        endTime = endTime,
        type = Steps,
    ).mapCatching { it as StepsAggregatedRecord }

/**
 * Aggregates [WeightRecord]s within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @return A [Result] containing a [WeightAggregatedRecord].
 */
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

// region Grouped Aggregate extensions
/**
 * Slices [ActiveEnergyBurnedRecord]s aggregate data within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @param sliceWidth The width of an individual slice of [startTime, endTime).
 * @return A [Result] containing a [ActiveEnergyBurnedAggregatedRecord].
 */
suspend fun HealthManager.aggregateActiveEnergyBurnedGroupByDuration(
    startTime: Instant,
    endTime: Instant,
    sliceWidth: Duration,
): Result<List<ActiveEnergyBurnedAggregatedRecord>> =
    aggregateGroupByDuration(
        startTime = startTime,
        endTime = endTime,
        sliceWidth = sliceWidth,
        type = ActiveEnergyBurned,
    ).map { it.filterIsInstance<ActiveEnergyBurnedAggregatedRecord>() }

/**
 * Slices [BloodGlucoseRecord]s aggregate data within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @param sliceWidth The width of an individual slice of [startTime, endTime).
 * @return A [Result] containing a [BloodGlucoseAggregatedRecord].
 */
suspend fun HealthManager.aggregateBloodGlucoseGroupByDuration(
    startTime: Instant,
    endTime: Instant,
    sliceWidth: Duration,
): Result<List<BloodGlucoseAggregatedRecord>> =
    aggregateGroupByDuration(
        startTime = startTime,
        endTime = endTime,
        sliceWidth = sliceWidth,
        type = BloodGlucose,
    ).map { it.filterIsInstance<BloodGlucoseAggregatedRecord>() }

/**
 * Slices [BloodPressureRecord]s aggregate data within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @param sliceWidth The width of an individual slice of [startTime, endTime).
 * @return A [Result] containing a [BloodPressureAggregatedRecord].
 */
suspend fun HealthManager.aggregateBloodPressureGroupByDuration(
    startTime: Instant,
    endTime: Instant,
    sliceWidth: Duration,
): Result<List<BloodPressureAggregatedRecord>> =
    aggregateGroupByDuration(
        startTime = startTime,
        endTime = endTime,
        sliceWidth = sliceWidth,
        type = BloodPressure,
    ).map { it.filterIsInstance<BloodPressureAggregatedRecord>() }

/**
 * Slices [BodyFatRecord]s aggregate data within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @param sliceWidth The width of an individual slice of [startTime, endTime).
 * @return A [Result] containing a [BodyFatAggregatedRecord].
 */
suspend fun HealthManager.aggregateBodyFatGroupByDuration(
    startTime: Instant,
    endTime: Instant,
    sliceWidth: Duration,
): Result<List<BodyFatAggregatedRecord>> =
    aggregateGroupByDuration(
        startTime = startTime,
        endTime = endTime,
        sliceWidth = sliceWidth,
        type = BodyFat,
    ).map { it.filterIsInstance<BodyFatAggregatedRecord>() }

/**
 * Slices [BodyTemperatureRecord]s aggregate data within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @param sliceWidth The width of an individual slice of [startTime, endTime).
 * @return A [Result] containing a [BodyTemperatureAggregatedRecord].
 */
suspend fun HealthManager.aggregateBodyTemperatureGroupByDuration(
    startTime: Instant,
    endTime: Instant,
    sliceWidth: Duration,
): Result<List<BodyTemperatureAggregatedRecord>> =
    aggregateGroupByDuration(
        startTime = startTime,
        endTime = endTime,
        sliceWidth = sliceWidth,
        type = BodyTemperature,
    ).map { it.filterIsInstance<BodyTemperatureAggregatedRecord>() }

/**
 * Slices [CyclingPedalingCadenceRecord]s aggregate data within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @param sliceWidth The width of an individual slice of [startTime, endTime).
 * @return A [Result] containing a [CyclingPedalingCadenceAggregatedRecord].
 */
suspend fun HealthManager.aggregateCyclingPedalingCadenceGroupByDuration(
    startTime: Instant,
    endTime: Instant,
    sliceWidth: Duration,
): Result<List<CyclingPedalingCadenceAggregatedRecord>> =
    aggregateGroupByDuration(
        startTime = startTime,
        endTime = endTime,
        sliceWidth = sliceWidth,
        type = CyclingPedalingCadence,
    ).map { it.filterIsInstance<CyclingPedalingCadenceAggregatedRecord>() }

/**
 * Slices [DistanceRecord]s aggregate data within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @param sliceWidth The width of an individual slice of [startTime, endTime).
 * @return A [Result] containing a [DistanceAggregatedRecord].
 */
suspend fun HealthManager.aggregateDistanceGroupByDuration(
    startTime: Instant,
    endTime: Instant,
    sliceWidth: Duration,
): Result<List<DistanceAggregatedRecord>> =
    aggregateGroupByDuration(
        startTime = startTime,
        endTime = endTime,
        sliceWidth = sliceWidth,
        type = Distance,
    ).map { it.filterIsInstance<DistanceAggregatedRecord>() }

/**
 * Slices [HeartRateRecord]s aggregate data within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @param sliceWidth The width of an individual slice of [startTime, endTime).
 * @return A [Result] containing a [HeartRateAggregatedRecord].
 */
suspend fun HealthManager.aggregateHeartRateGroupByDuration(
    startTime: Instant,
    endTime: Instant,
    sliceWidth: Duration,
): Result<List<HeartRateAggregatedRecord>> =
    aggregateGroupByDuration(
        startTime = startTime,
        endTime = endTime,
        sliceWidth = sliceWidth,
        type = HeartRate,
    ).map { it.filterIsInstance<HeartRateAggregatedRecord>() }

/**
 * Slices [HeightRecord]s aggregate data within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @param sliceWidth The width of an individual slice of [startTime, endTime).
 * @return A [Result] containing a [HeightAggregatedRecord].
 */
suspend fun HealthManager.aggregateHeightGroupByDuration(
    startTime: Instant,
    endTime: Instant,
    sliceWidth: Duration,
): Result<List<HeightAggregatedRecord>> =
    aggregateGroupByDuration(
        startTime = startTime,
        endTime = endTime,
        sliceWidth = sliceWidth,
        type = Height,
    ).map { it.filterIsInstance<HeightAggregatedRecord>() }

/**
 * Slices [LeanBodyMassRecord]s aggregate data within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @param sliceWidth The width of an individual slice of [startTime, endTime).
 * @return A [Result] containing a [LeanBodyMassAggregatedRecord].
 */
suspend fun HealthManager.aggregateLeanBodyMassGroupByDuration(
    startTime: Instant,
    endTime: Instant,
    sliceWidth: Duration,
): Result<List<LeanBodyMassAggregatedRecord>> =
    aggregateGroupByDuration(
        startTime = startTime,
        endTime = endTime,
        sliceWidth = sliceWidth,
        type = LeanBodyMass,
    ).map { it.filterIsInstance<LeanBodyMassAggregatedRecord>() }

/**
 * Slices [PowerRecord]s aggregate data within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @param sliceWidth The width of an individual slice of [startTime, endTime).
 * @return A [Result] containing a [PowerAggregatedRecord].
 */
suspend fun HealthManager.aggregatePowerGroupByDuration(
    startTime: Instant,
    endTime: Instant,
    sliceWidth: Duration,
): Result<List<PowerAggregatedRecord>> =
    aggregateGroupByDuration(
        startTime = startTime,
        endTime = endTime,
        sliceWidth = sliceWidth,
        type = Power,
    ).map { it.filterIsInstance<PowerAggregatedRecord>() }

/**
 * Slices [SleepSessionRecord]s aggregate data within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @param sliceWidth The width of an individual slice of [startTime, endTime).
 * @return A [Result] containing a [SleepAggregatedRecord].
 */
suspend fun HealthManager.aggregateSleepGroupByDuration(
    startTime: Instant,
    endTime: Instant,
    sliceWidth: Duration,
): Result<List<SleepAggregatedRecord>> =
    aggregateGroupByDuration(
        startTime = startTime,
        endTime = endTime,
        sliceWidth = sliceWidth,
        type = Sleep,
    ).map { it.filterIsInstance<SleepAggregatedRecord>() }

/**
 * Slices [StepsRecord]s aggregate data within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @param sliceWidth The width of an individual slice of [startTime, endTime).
 * @return A [Result] containing a [StepsAggregatedRecord].
 */
suspend fun HealthManager.aggregateStepsGroupByDuration(
    startTime: Instant,
    endTime: Instant,
    sliceWidth: Duration,
): Result<List<StepsAggregatedRecord>> =
    aggregateGroupByDuration(
        startTime = startTime,
        endTime = endTime,
        sliceWidth = sliceWidth,
        type = Steps,
    ).map { it.filterIsInstance<StepsAggregatedRecord>() }

/**
 * Slices [WeightRecord]s aggregate data within the specified time range.
 *
 * @param startTime The start time of the range (inclusive).
 * @param endTime The end time of the range (exclusive).
 * @param sliceWidth The width of an individual slice of [startTime, endTime).
 * @return A [Result] containing a [WeightAggregatedRecord].
 */
suspend fun HealthManager.aggregateWeightGroupByDuration(
    startTime: Instant,
    endTime: Instant,
    sliceWidth: Duration,
): Result<List<WeightAggregatedRecord>> =
    aggregateGroupByDuration(
        startTime = startTime,
        endTime = endTime,
        sliceWidth = sliceWidth,
        type = Weight,
    ).map { it.filterIsInstance<WeightAggregatedRecord>() }
// endregion