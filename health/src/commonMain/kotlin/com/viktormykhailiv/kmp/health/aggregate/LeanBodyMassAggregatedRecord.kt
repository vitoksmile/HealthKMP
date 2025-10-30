package com.viktormykhailiv.kmp.health.aggregate

import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.LeanBodyMass
import com.viktormykhailiv.kmp.health.units.Mass
import kotlin.time.Instant

/**
 * Captures the aggregated user's lean body mass.
 *
 * @param avg Average lean body mass.
 * @param min Minimum lean body mass.
 * @param max Maximum lean body mass.
 *
 * @see [Mass] for supported units.
 */
data class LeanBodyMassAggregatedRecord(
    val startTime: Instant,
    val endTime: Instant,
    val avg: Mass,
    val min: Mass,
    val max: Mass,
) : HealthAggregatedRecord {

    override val dataType: HealthDataType = LeanBodyMass
}
