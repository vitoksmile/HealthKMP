package com.viktormykhailiv.kmp.health.aggregate

import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.units.Mass
import kotlinx.datetime.Instant

/**
 * Captures the aggregated user's weight.
 *
 * @param avg Average weight.
 * @param min Minimum weight.
 * @param max Maximum weight.
 *
 * @see [Mass] for supported units.
 */
data class WeightAggregatedRecord(
    val startTime: Instant,
    val endTime: Instant,
    val avg: Mass,
    val min: Mass,
    val max: Mass,
) : HealthAggregatedRecord {

    override val dataType: HealthDataType = Weight
}
