package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.LeanBodyMass
import com.viktormykhailiv.kmp.health.InstantaneousRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.requireNotLess
import com.viktormykhailiv.kmp.health.requireNotMore
import com.viktormykhailiv.kmp.health.units.Mass
import com.viktormykhailiv.kmp.health.units.kilograms
import kotlinx.datetime.Instant

/**
 * Captures the user's lean body mass. Each record represents a single instantaneous measurement.
 *
 * @param mass Mass in [Mass] unit. Required field. Valid range: 0-1000 kilograms.
 */
data class LeanBodyMassRecord(
    override val time: Instant,
    val mass: Mass,
    override val metadata: Metadata,
) : InstantaneousRecord {

    override val dataType: HealthDataType = LeanBodyMass

    init {
        mass.requireNotLess(other = mass.zero(), name = "mass")
        mass.requireNotMore(other = MAX_MASS, name = "mass")
    }

    private companion object {
        private val MAX_MASS = 1000.kilograms
    }

}
