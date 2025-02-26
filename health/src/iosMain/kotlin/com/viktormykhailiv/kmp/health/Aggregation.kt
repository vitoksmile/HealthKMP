package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.aggregate.HeartRateAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.SleepAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.StepsAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.WeightAggregatedRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.units.Mass
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import platform.HealthKit.HKQuantityType
import platform.HealthKit.HKQuantityTypeIdentifierBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierHeartRate
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

internal fun HealthDataType.toHKQuantityType(): HKQuantityType? = when (this) {
    HeartRate ->
        HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRate)

    Sleep ->
        throw IllegalArgumentException("Sleep is not supported for aggregation")

    Steps ->
        HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierStepCount)

    Weight ->
        HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyMass)
}

/**
 * Note: following `AggregateMetric` must be aligned with [toHealthAggregatedRecord].
 */
internal fun HealthDataType.toHKStatisticOptions(): HKStatisticsOptions = when (this) {
    HeartRate ->
        HKStatisticsOptionDiscreteAverage or HKStatisticsOptionDiscreteMin or HKStatisticsOptionDiscreteMax

    Sleep ->
        throw IllegalArgumentException("Sleep is not supported for aggregation")

    Steps ->
        HKStatisticsOptionCumulativeSum

    Weight ->
        HKStatisticsOptionDiscreteAverage or HKStatisticsOptionDiscreteMin or HKStatisticsOptionDiscreteMax
}

/**
 * Note: following `AggregateMetric` must be aligned with [toHKStatisticOptions].
 */
internal fun HKStatistics.toHealthAggregatedRecord(): HealthAggregatedRecord? {
    return when (quantityType.identifier) {
        HKQuantityTypeIdentifierHeartRate -> {
            HeartRateAggregatedRecord(
                startTime = startDate.toKotlinInstant(),
                endTime = endDate.toKotlinInstant(),
                avg = averageQuantity()?.doubleValueForUnit(heartRateUnit)?.toLong() ?: 0L,
                min = minimumQuantity()?.doubleValueForUnit(heartRateUnit)?.toLong() ?: 0L,
                max = maximumQuantity()?.doubleValueForUnit(heartRateUnit)?.toLong() ?: 0L,
            )
        }

        HKQuantityTypeIdentifierStepCount -> {
            StepsAggregatedRecord(
                startTime = startDate.toKotlinInstant(),
                endTime = endDate.toKotlinInstant(),
                count = sumQuantity()?.doubleValueForUnit(HKUnit.countUnit())?.toLong() ?: 0L,
            )
        }

        HKQuantityTypeIdentifierBodyMass -> {
            WeightAggregatedRecord(
                startTime = startDate.toKotlinInstant(),
                endTime = endDate.toKotlinInstant(),
                avg = Mass.pounds(averageQuantity()?.doubleValueForUnit(HKUnit.poundUnit()) ?: 0.0),
                min = Mass.pounds(minimumQuantity()?.doubleValueForUnit(HKUnit.poundUnit()) ?: 0.0),
                max = Mass.pounds(maximumQuantity()?.doubleValueForUnit(HKUnit.poundUnit()) ?: 0.0),
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
