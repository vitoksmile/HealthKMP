package com.viktormykhailiv.kmp.health.aggregate

import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import kotlin.time.Instant

/**
 * Captures the aggregated number of steps.
 */
data class StepsAggregatedRecord(
    val startTime: Instant,
    val endTime: Instant,
    val count: Long,
) : HealthAggregatedRecord {

    override val dataType: HealthDataType = Steps
}
