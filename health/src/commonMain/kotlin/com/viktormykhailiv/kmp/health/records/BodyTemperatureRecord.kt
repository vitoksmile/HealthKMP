package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.BodyTemperature
import com.viktormykhailiv.kmp.health.InstantaneousRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.requireNotLess
import com.viktormykhailiv.kmp.health.requireNotMore
import com.viktormykhailiv.kmp.health.units.Temperature
import com.viktormykhailiv.kmp.health.units.celsius
import kotlinx.datetime.Instant

/**
 * Captures the body temperature of a user. Each record represents a single instantaneous body
 * temperature measurement.
 *
 * @param temperature Temperature in [Temperature] unit. Valid range: 0-100 Celsius degrees.
 * @param measurementLocation Where on the user's body the temperature measurement was taken from.
 */
data class BodyTemperatureRecord(
    override val time: Instant,
    val temperature: Temperature,
    val measurementLocation: MeasurementLocation?,
    override val metadata: Metadata,
) : InstantaneousRecord {

    override val dataType: HealthDataType = BodyTemperature

    init {
        temperature.requireNotLess(other = 0.celsius, name = "temperature")
        temperature.requireNotMore(other = 100.celsius, name = "temperature")
    }

    enum class MeasurementLocation {
        Armpit,
        Finger,
        Forehead,
        Mouth,
        Rectum,
        TemporalArtery,
        Toe,
        Ear,
        Wrist,
        Vagina,
    }

}
