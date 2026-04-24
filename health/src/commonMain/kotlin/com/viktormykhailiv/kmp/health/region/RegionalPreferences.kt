package com.viktormykhailiv.kmp.health.region

/**
 * Represents regional preferences of the user.
 *
 * @param temperature The preferred unit for temperature.
 */
data class RegionalPreferences(
    val temperature: TemperatureRegionalPreference,
)
