@file:OptIn(UnsafeNumber::class)

package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.HealthDataType.BloodPressure
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Height
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.aggregate.BloodPressureAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.HeartRateAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.HeightAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.SleepAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.StepsAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.WeightAggregatedRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.units.Length
import com.viktormykhailiv.kmp.health.units.Mass
import com.viktormykhailiv.kmp.health.units.Pressure
import kotlinx.cinterop.UnsafeNumber
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import platform.HealthKit.HKQuantityType
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureDiastolic
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureSystolic
import platform.HealthKit.HKQuantityTypeIdentifierBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierHeartRate
import platform.HealthKit.HKQuantityTypeIdentifierHeight
import platform.HealthKit.HKQuantityTypeIdentifierStepCount
import platform.HealthKit.HKStatistics
import platform.HealthKit.HKStatisticsOptionCumulativeSum
import platform.HealthKit.HKStatisticsOptionDiscreteAverage
import platform.HealthKit.HKStatisticsOptionDiscreteMax
import platform.HealthKit.HKStatisticsOptionDiscreteMin
import platform.HealthKit.HKStatisticsOptions
import platform.HealthKit.HKUnit
import platform.HealthKit.countUnit
import platform.HealthKit.poundUnit
import kotlin.time.Duration.Companion.seconds

internal fun HealthDataType.toHKQuantityType(): List<HKQuantityType?> = when (this) {
    BloodPressure ->
        listOf(
            HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureSystolic),
            HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureDiastolic),
        )

    HeartRate ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRate))

    Height ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeight))

    Sleep ->
        throw IllegalArgumentException("Sleep is not supported for aggregation")

    Steps ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierStepCount))

    Weight ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyMass))
}

/**
 * Note: following `AggregateMetric` must be aligned with [toHealthAggregatedRecord].
 */
internal fun HealthDataType.toHKStatisticOptions(): HKStatisticsOptions = when (this) {
    BloodPressure ->
        discreteStatisticsOptions()

    HeartRate ->
        discreteStatisticsOptions()

    Height ->
        discreteStatisticsOptions()

    Sleep ->
        throw IllegalArgumentException("Sleep is not supported for aggregation")

    Steps ->
        HKStatisticsOptionCumulativeSum

    Weight ->
        discreteStatisticsOptions()
}

private fun discreteStatisticsOptions(): HKStatisticsOptions {
    return HKStatisticsOptionDiscreteAverage or HKStatisticsOptionDiscreteMin or HKStatisticsOptionDiscreteMax
}

/**
 * Note: following `AggregateMetric` must be aligned with [toHKStatisticOptions].
 */
internal fun List<HKStatistics>.toHealthAggregatedRecord(): HealthAggregatedRecord? {
    val record = first()
    return when (record.quantityType.identifier) {
        HKQuantityTypeIdentifierBloodPressureSystolic,
        HKQuantityTypeIdentifierBloodPressureDiastolic -> {
            val systolic =
                first { it.quantityType.identifier == HKQuantityTypeIdentifierBloodPressureSystolic }
            val diastolic =
                first { it.quantityType.identifier == HKQuantityTypeIdentifierBloodPressureDiastolic }

            BloodPressureAggregatedRecord(
                startTime = record.startDate.toKotlinInstant(),
                endTime = record.endDate.toKotlinInstant(),
                systolic = BloodPressureAggregatedRecord.AggregatedRecord(
                    avg = Pressure.millimetersOfMercury(
                        systolic.averageQuantity()?.doubleValueForUnit(bloodPressureUnit) ?: 0.0,
                    ),
                    min = Pressure.millimetersOfMercury(
                        systolic.minimumQuantity()?.doubleValueForUnit(bloodPressureUnit) ?: 0.0,
                    ),
                    max = Pressure.millimetersOfMercury(
                        systolic.maximumQuantity()?.doubleValueForUnit(bloodPressureUnit) ?: 0.0,
                    ),
                ),
                diastolic = BloodPressureAggregatedRecord.AggregatedRecord(
                    avg = Pressure.millimetersOfMercury(
                        diastolic.averageQuantity()?.doubleValueForUnit(bloodPressureUnit) ?: 0.0,
                    ),
                    min = Pressure.millimetersOfMercury(
                        diastolic.minimumQuantity()?.doubleValueForUnit(bloodPressureUnit) ?: 0.0,
                    ),
                    max = Pressure.millimetersOfMercury(
                        diastolic.maximumQuantity()?.doubleValueForUnit(bloodPressureUnit) ?: 0.0,
                    ),
                ),
            )
        }

        HKQuantityTypeIdentifierHeartRate -> {
            HeartRateAggregatedRecord(
                startTime = record.startDate.toKotlinInstant(),
                endTime = record.endDate.toKotlinInstant(),
                avg = record.averageQuantity()?.doubleValueForUnit(heartRateUnit)?.toLong() ?: 0L,
                min = record.minimumQuantity()?.doubleValueForUnit(heartRateUnit)?.toLong() ?: 0L,
                max = record.maximumQuantity()?.doubleValueForUnit(heartRateUnit)?.toLong() ?: 0L,
            )
        }

        HKQuantityTypeIdentifierHeight -> {
            HeightAggregatedRecord(
                startTime = record.startDate.toKotlinInstant(),
                endTime = record.endDate.toKotlinInstant(),
                avg = Length.meters(
                    record.averageQuantity()?.doubleValueForUnit(heightUnit) ?: 0.0
                ),
                min = Length.meters(
                    record.minimumQuantity()?.doubleValueForUnit(heightUnit) ?: 0.0
                ),
                max = Length.meters(
                    record.maximumQuantity()?.doubleValueForUnit(heightUnit) ?: 0.0
                ),
            )
        }

        HKQuantityTypeIdentifierStepCount -> {
            StepsAggregatedRecord(
                startTime = record.startDate.toKotlinInstant(),
                endTime = record.endDate.toKotlinInstant(),
                count = record.sumQuantity()?.doubleValueForUnit(HKUnit.countUnit())?.toLong()
                    ?: 0L,
            )
        }

        HKQuantityTypeIdentifierBodyMass -> {
            WeightAggregatedRecord(
                startTime = record.startDate.toKotlinInstant(),
                endTime = record.endDate.toKotlinInstant(),
                avg = Mass.pounds(
                    record.averageQuantity()?.doubleValueForUnit(HKUnit.poundUnit()) ?: 0.0
                ),
                min = Mass.pounds(
                    record.minimumQuantity()?.doubleValueForUnit(HKUnit.poundUnit()) ?: 0.0
                ),
                max = Mass.pounds(
                    record.maximumQuantity()?.doubleValueForUnit(HKUnit.poundUnit()) ?: 0.0
                ),
            )
        }

        else -> null
    }
}

internal fun List<SleepSessionRecord>.aggregate(
    startTime: Instant,
    endTime: Instant,
): HealthAggregatedRecord {
    if (isEmpty()) {
        return SleepAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            totalDuration = 0.seconds,
        )
    }

    return SleepAggregatedRecord(
        startTime = startTime,
        endTime = endTime,
        totalDuration = sumOf { it.duration.inWholeSeconds }.seconds,
    )
}
