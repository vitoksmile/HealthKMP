@file:OptIn(UnsafeNumber::class)

package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.HealthDataType.BloodGlucose
import com.viktormykhailiv.kmp.health.HealthDataType.BloodPressure
import com.viktormykhailiv.kmp.health.HealthDataType.BodyFat
import com.viktormykhailiv.kmp.health.HealthDataType.BodyTemperature
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
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.region.TemperatureRegionalPreference
import kotlinx.cinterop.UnsafeNumber
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import platform.HealthKit.HKQuantityType
import platform.HealthKit.HKQuantityTypeIdentifierBloodGlucose
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureDiastolic
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureSystolic
import platform.HealthKit.HKQuantityTypeIdentifierBodyFatPercentage
import platform.HealthKit.HKQuantityTypeIdentifierBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierBodyTemperature
import platform.HealthKit.HKQuantityTypeIdentifierHeartRate
import platform.HealthKit.HKQuantityTypeIdentifierHeight
import platform.HealthKit.HKQuantityTypeIdentifierLeanBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierStepCount
import platform.HealthKit.HKStatistics
import platform.HealthKit.HKStatisticsOptionCumulativeSum
import platform.HealthKit.HKStatisticsOptionDiscreteAverage
import platform.HealthKit.HKStatisticsOptionDiscreteMax
import platform.HealthKit.HKStatisticsOptionDiscreteMin
import platform.HealthKit.HKStatisticsOptions
import kotlin.time.Duration.Companion.seconds

internal fun HealthDataType.toHKQuantityType(): List<HKQuantityType?> = when (this) {
    BloodGlucose ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodGlucose))

    BloodPressure ->
        listOf(
            HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureSystolic),
            HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureDiastolic),
        )

    BodyFat ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyFatPercentage))

    BodyTemperature ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyTemperature))

    HeartRate ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRate))

    Height ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeight))

    LeanBodyMass ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierLeanBodyMass))

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
    BloodGlucose ->
        discreteStatisticsOptions()

    BloodPressure ->
        discreteStatisticsOptions()

    BodyFat ->
        discreteStatisticsOptions()

    BodyTemperature ->
        discreteStatisticsOptions()

    HeartRate ->
        discreteStatisticsOptions()

    Height ->
        discreteStatisticsOptions()

    LeanBodyMass ->
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
internal suspend fun List<HKStatistics>.toHealthAggregatedRecord(
    temperaturePreference: suspend () -> TemperatureRegionalPreference,
): HealthAggregatedRecord? {
    val record = first()
    return when (record.quantityType.identifier) {
        HKQuantityTypeIdentifierBloodGlucose -> {
            BloodGlucoseAggregatedRecord(
                startTime = record.startDate.toKotlinInstant(),
                endTime = record.endDate.toKotlinInstant(),
                avg = record.averageQuantity().bloodGlucoseValue,
                min = record.minimumQuantity().bloodGlucoseValue,
                max = record.maximumQuantity().bloodGlucoseValue,
            )
        }

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
                    avg = systolic.averageQuantity().bloodPressureValue,
                    min = systolic.minimumQuantity().bloodPressureValue,
                    max = systolic.maximumQuantity().bloodPressureValue,
                ),
                diastolic = BloodPressureAggregatedRecord.AggregatedRecord(
                    avg = diastolic.averageQuantity().bloodPressureValue,
                    min = diastolic.minimumQuantity().bloodPressureValue,
                    max = diastolic.maximumQuantity().bloodPressureValue,
                ),
            )
        }

        HKQuantityTypeIdentifierBodyFatPercentage -> {
            BodyFatAggregatedRecord(
                startTime = record.startDate.toKotlinInstant(),
                endTime = record.endDate.toKotlinInstant(),
                avg = record.averageQuantity().bodyFatValue,
                min = record.minimumQuantity().bodyFatValue,
                max = record.maximumQuantity().bodyFatValue,
            )
        }

        HKQuantityTypeIdentifierBodyTemperature -> {
            BodyTemperatureAggregatedRecord(
                startTime = record.startDate.toKotlinInstant(),
                endTime = record.endDate.toKotlinInstant(),
                avg = record.averageQuantity().bodyTemperatureValue.preferred(temperaturePreference()),
                min = record.minimumQuantity().bodyTemperatureValue.preferred(temperaturePreference()),
                max = record.maximumQuantity().bodyTemperatureValue.preferred(temperaturePreference()),
            )
        }

        HKQuantityTypeIdentifierHeartRate -> {
            HeartRateAggregatedRecord(
                startTime = record.startDate.toKotlinInstant(),
                endTime = record.endDate.toKotlinInstant(),
                avg = record.averageQuantity().heartRateValue,
                min = record.minimumQuantity().heartRateValue,
                max = record.maximumQuantity().heartRateValue,
            )
        }

        HKQuantityTypeIdentifierHeight -> {
            HeightAggregatedRecord(
                startTime = record.startDate.toKotlinInstant(),
                endTime = record.endDate.toKotlinInstant(),
                avg = record.averageQuantity().heightValue,
                min = record.minimumQuantity().heightValue,
                max = record.maximumQuantity().heightValue,
            )
        }

        HKQuantityTypeIdentifierLeanBodyMass -> {
            LeanBodyMassAggregatedRecord(
                startTime = record.startDate.toKotlinInstant(),
                endTime = record.endDate.toKotlinInstant(),
                avg = record.averageQuantity().massValue,
                min = record.minimumQuantity().massValue,
                max = record.maximumQuantity().massValue,
            )
        }

        HKQuantityTypeIdentifierStepCount -> {
            StepsAggregatedRecord(
                startTime = record.startDate.toKotlinInstant(),
                endTime = record.endDate.toKotlinInstant(),
                count = record.sumQuantity().stepsValue,
            )
        }

        HKQuantityTypeIdentifierBodyMass -> {
            WeightAggregatedRecord(
                startTime = record.startDate.toKotlinInstant(),
                endTime = record.endDate.toKotlinInstant(),
                avg = record.averageQuantity().massValue,
                min = record.minimumQuantity().massValue,
                max = record.maximumQuantity().massValue,
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
