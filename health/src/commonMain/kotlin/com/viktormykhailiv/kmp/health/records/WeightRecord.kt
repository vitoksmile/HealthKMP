package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.InstantaneousRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.requireNotLess
import com.viktormykhailiv.kmp.health.requireNotMore
import com.viktormykhailiv.kmp.health.units.Mass
import com.viktormykhailiv.kmp.health.units.kilograms
import kotlin.time.Instant

/**
 * Captures the user's weight.
 *
 * @see [Mass] for supported units.
 */
data class WeightRecord(
    override val time: Instant,
    val weight: Mass,
    override val metadata: Metadata,
) : InstantaneousRecord {

    override val dataType: HealthDataType = Weight

    init {
        weight.requireNotLess(other = weight.zero(), name = "weight")
        weight.requireNotMore(other = 1000.kilograms, name = "weight")
    }
}