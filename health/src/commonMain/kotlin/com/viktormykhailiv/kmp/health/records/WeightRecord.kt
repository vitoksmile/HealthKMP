package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.InstantaneousRecord
import com.viktormykhailiv.kmp.health.requireNotLess
import com.viktormykhailiv.kmp.health.requireNotMore
import com.viktormykhailiv.kmp.health.units.Mass
import com.viktormykhailiv.kmp.health.units.kilograms
import kotlinx.datetime.Instant

/**
 * Captures the user's weight.
 *
 * See [Mass] for supported units.
 */
data class WeightRecord(
    override val time: Instant,
    val weight: Mass,
) : InstantaneousRecord {

    override val dataType: HealthDataType = Weight

    init {
        weight.requireNotLess(other = weight.zero(), name = "weight")
        weight.requireNotMore(other = 1000.kilograms, name = "weight")
    }
}