package com.viktormykhailiv.kmp.health.aggregate

import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.IntervalRecord
import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * Captures the aggregated total duration of sleep sessions.
 */
class SleepAggregatedRecord(
    override val startTime: Instant,
    override val endTime: Instant,
    val totalDuration: Duration,
) : HealthAggregatedRecord,
    IntervalRecord {

    override val dataType: HealthDataType = Sleep
}
