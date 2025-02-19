package com.viktormykhailiv.kmp.health.aggregate

import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.IntervalRecord
import kotlinx.datetime.Instant

/**
 * Captures the aggregated number of steps.
 */
class StepsAggregatedRecord(
    override val startTime: Instant,
    override val endTime: Instant,
    val count: Long,
) : HealthAggregatedRecord,
    IntervalRecord {

    override val dataType: HealthDataType = Steps
}
