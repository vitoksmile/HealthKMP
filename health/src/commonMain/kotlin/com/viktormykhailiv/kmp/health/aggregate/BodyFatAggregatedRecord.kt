package com.viktormykhailiv.kmp.health.aggregate

import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.BodyFat
import com.viktormykhailiv.kmp.health.units.Percentage
import kotlinx.datetime.Instant

/**
 * Captures the aggregated user's body fat.
 *
 * @param avg Average body fat.
 * @param min Minimum body fat.
 * @param max Maximum body fat.
 */
data class BodyFatAggregatedRecord(
    val startTime: Instant,
    val endTime: Instant,
    val avg: Percentage,
    val min: Percentage,
    val max: Percentage,
) : HealthAggregatedRecord {

    override val dataType: HealthDataType = BodyFat
}
