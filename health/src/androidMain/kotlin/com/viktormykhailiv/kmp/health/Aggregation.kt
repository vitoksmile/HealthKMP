package com.viktormykhailiv.kmp.health

import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.aggregate.AggregationResult
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Height
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.aggregate.HeartRateAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.HeightAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.SleepAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.StepsAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.WeightAggregatedRecord
import com.viktormykhailiv.kmp.health.units.Length
import com.viktormykhailiv.kmp.health.units.kilograms
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toKotlinDuration

/**
 * Note: following `AggregateMetric` must be aligned with [toHealthAggregatedRecord].
 */
internal fun HealthDataType.toAggregateMetrics(): Set<AggregateMetric<Any>> = when (this) {
    HeartRate ->
        setOf(HeartRateRecord.BPM_AVG, HeartRateRecord.BPM_MIN, HeartRateRecord.BPM_MAX)

    Height ->
        setOf(HeightRecord.HEIGHT_AVG, HeightRecord.HEIGHT_MIN, HeightRecord.HEIGHT_MAX)

    Sleep ->
        setOf(SleepSessionRecord.SLEEP_DURATION_TOTAL)

    Steps ->
        setOf(StepsRecord.COUNT_TOTAL)

    Weight ->
        setOf(WeightRecord.WEIGHT_AVG, WeightRecord.WEIGHT_MIN, WeightRecord.WEIGHT_MAX)
}

/**
 * Note: following `AggregateMetric` must be aligned with [toAggregateMetrics].
 */
internal fun AggregationResult.toHealthAggregatedRecord(
    startTime: Instant,
    endTime: Instant,
    type: HealthDataType,
): HealthAggregatedRecord = when (type) {
    is HeartRate -> {
        HeartRateAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            avg = get(HeartRateRecord.BPM_AVG) ?: 0L,
            min = get(HeartRateRecord.BPM_MIN) ?: 0L,
            max = get(HeartRateRecord.BPM_MAX) ?: 0L,
        )
    }

    is Height -> {
        HeightAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            avg = Length.meters(get(HeightRecord.HEIGHT_AVG)?.inMeters ?: 0.0),
            min = Length.meters(get(HeightRecord.HEIGHT_MIN)?.inMeters ?: 0.0),
            max = Length.meters(get(HeightRecord.HEIGHT_MAX)?.inMeters ?: 0.0),
        )
    }

    is Sleep -> {
        SleepAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            totalDuration = get(SleepSessionRecord.SLEEP_DURATION_TOTAL)?.toKotlinDuration()
                ?: 0.seconds,
        )
    }

    is Steps -> {
        StepsAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            count = get(StepsRecord.COUNT_TOTAL) ?: 0L,
        )
    }

    is Weight -> {
        WeightAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            avg = get(WeightRecord.WEIGHT_AVG)?.toMass() ?: 0.kilograms,
            min = get(WeightRecord.WEIGHT_MIN)?.toMass() ?: 0.kilograms,
            max = get(WeightRecord.WEIGHT_MAX)?.toMass() ?: 0.kilograms,
        )
    }
}
