package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.MenstruationPeriod
import com.viktormykhailiv.kmp.health.IntervalRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

/**
 * Captures user's menstruation periods.
 */
data class MenstruationPeriodRecord(
    override val startTime: Instant,
    override val endTime: Instant,
    override val metadata: Metadata,
) : IntervalRecord {

    override val dataType: HealthDataType = MenstruationPeriod

    init {
        require(startTime < endTime) { "startTime must be before endTime." }
        require(endTime - startTime <= MAX_DURATION) { "Period must not exceed 31 days" }
    }

    private companion object {
        private val MAX_DURATION = 31.days
    }

}
