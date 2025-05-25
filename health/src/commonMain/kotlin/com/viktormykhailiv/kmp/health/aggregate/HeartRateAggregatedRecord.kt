package com.viktormykhailiv.kmp.health.aggregate

import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.IntervalRecord
import kotlinx.datetime.Instant

/**
 * Captures the aggregated user's heart rate.
 *
 * @param avg Average heart rate.
 * @param min Minimum heart rate.
 * @param max Maximum heart rate.
 */
data class HeartRateAggregatedRecord(
    val startTime: Instant,
    val endTime: Instant,
    val avg: Long,
    val min: Long,
    val max: Long,
) : HealthAggregatedRecord {

    override val dataType: HealthDataType = HeartRate
}
