package com.viktormykhailiv.kmp.health.region

import com.viktormykhailiv.kmp.health.units.Temperature

/**
 * Represents the preferred unit for temperature in a specific region.
 */
enum class TemperatureRegionalPreference {
    /** Celsius. */
    Celsius,

    /** Fahrenheit. */
    Fahrenheit,
}

/**
 * Returns a [Temperature] object in the preferred unit based on the given [preference].
 */
fun Temperature.preferred(preference: TemperatureRegionalPreference): Temperature =
    when (preference) {
        TemperatureRegionalPreference.Celsius -> Temperature.celsius(inCelsius)
        TemperatureRegionalPreference.Fahrenheit -> Temperature.fahrenheit(inFahrenheit)
    }
