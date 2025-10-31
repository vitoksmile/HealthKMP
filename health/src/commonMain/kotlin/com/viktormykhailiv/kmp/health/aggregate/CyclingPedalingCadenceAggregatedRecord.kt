package com.viktormykhailiv.kmp.health.aggregate

import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.CyclingPedalingCadence
import kotlin.time.Instant

/**
 * Captures the aggregated user's pedaling cadence rate.
 *
 * @param avg Average rpm.
 * @param min Minimum rpm.
 * @param max Maximum rpm.
 */
data class CyclingPedalingCadenceAggregatedRecord(
    val startTime: Instant,
    val endTime: Instant,
    val avg: Double,
    val min: Double,
    val max: Double,
) : HealthAggregatedRecord {

    override val dataType: HealthDataType = CyclingPedalingCadence
}
