package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.BodyFat
import com.viktormykhailiv.kmp.health.InstantaneousRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.requireNonNegative
import com.viktormykhailiv.kmp.health.requireNotMore
import com.viktormykhailiv.kmp.health.units.Percentage
import com.viktormykhailiv.kmp.health.units.percent
import kotlinx.datetime.Instant

/**
 * Captures the body fat percentage of a user. Each record represents a person's total body fat as a
 * percentage of their total body mass.
 *
 * @param percentage Percentage. Required field. Valid range: 0-100.
 */
data class BodyFatRecord(
    override val time: Instant,
    val percentage: Percentage,
    override val metadata: Metadata,
) : InstantaneousRecord {

    override val dataType: HealthDataType = BodyFat

    init {
        requireNonNegative(value = percentage.value, name = "percentage")
        percentage.requireNotMore(other = MAX_PERCENTAGE, name = "percentage")
    }

    private companion object {
        private val MAX_PERCENTAGE = 100.percent
    }

}
