package com.vitoksmile.kmp.health.records

import com.vitoksmile.kmp.health.InstantaneousRecord
import com.vitoksmile.kmp.health.units.Mass
import kotlinx.datetime.Instant

/**
 * Captures the user's weight.
 *
 * See [Mass] for supported units.
 */
data class WeightRecord(
    override val time: Instant,
    val weight: Mass,
) : InstantaneousRecord