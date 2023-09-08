package com.vitoksmile.kmp.health.records

import com.vitoksmile.kmp.health.HealthDataType
import com.vitoksmile.kmp.health.HealthDataType.Steps
import com.vitoksmile.kmp.health.IntervalRecord
import kotlinx.datetime.Instant

/**
 * Captures the number of steps taken since the last reading. Each step is only reported once so
 * records shouldn't have overlapping time. The start time of each record should represent the start
 * of the interval in which steps were taken.
 *
 * The start time must be equal to or greater than the end time of the previous record. Adding all
 * of the values together for a period of time calculates the total number of steps during that
 * period.
 *
 * @param count Valid range: 1-1_000_000
 */
data class StepsRecord(
    override val startTime: Instant,
    override val endTime: Instant,
    val count: Int,
) : IntervalRecord {

    override val dataType: HealthDataType = Steps
}