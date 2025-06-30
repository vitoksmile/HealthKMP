package com.viktormykhailiv.kmp.health.aggregate

import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.units.Length
import kotlinx.datetime.Instant

/**
 * Captures the aggregated user's height.
 *
 * @param avg Average height.
 * @param min Minimum height.
 * @param max Maximum height.
 */
data class HeightAggregatedRecord(
    val startTime: Instant,
    val endTime: Instant,
    val avg: Length,
    val min: Length,
    val max: Length,
) : HealthAggregatedRecord {

    override val dataType: HealthDataType = HeartRate
}
