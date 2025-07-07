package com.viktormykhailiv.kmp.health.aggregate

import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.BodyTemperature
import com.viktormykhailiv.kmp.health.units.Temperature
import kotlinx.datetime.Instant

/**
 * Captures the aggregated user's body temperature.
 *
 * @param avg Average body temperature.
 * @param min Minimum body temperature.
 * @param max Maximum body temperature.
 */
data class BodyTemperatureAggregatedRecord(
    val startTime: Instant,
    val endTime: Instant,
    val avg: Temperature,
    val min: Temperature,
    val max: Temperature,
) : HealthAggregatedRecord {

    override val dataType: HealthDataType = BodyTemperature
}
