package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.InstantaneousRecord
import com.viktormykhailiv.kmp.health.units.Mass
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
}