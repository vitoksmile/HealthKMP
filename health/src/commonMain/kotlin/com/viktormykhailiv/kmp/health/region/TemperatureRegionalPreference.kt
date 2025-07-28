package com.viktormykhailiv.kmp.health.region

import com.viktormykhailiv.kmp.health.units.Temperature

enum class TemperatureRegionalPreference {
    Celsius,
    Fahrenheit,
}

fun Temperature.preferred(preference: TemperatureRegionalPreference): Temperature =
    when (preference) {
        TemperatureRegionalPreference.Celsius -> Temperature.celsius(inCelsius)
        TemperatureRegionalPreference.Fahrenheit -> Temperature.fahrenheit(inFahrenheit)
    }
