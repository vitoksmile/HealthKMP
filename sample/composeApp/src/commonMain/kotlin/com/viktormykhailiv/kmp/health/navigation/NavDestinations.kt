package com.viktormykhailiv.kmp.health.navigation

import kotlinx.serialization.Serializable

sealed interface NavDestinations {

    @Serializable
    data object Root : NavDestinations

    @Serializable
    data object BloodGlucose : NavDestinations

    @Serializable
    data object BloodPressure : NavDestinations

    @Serializable
    data object BodyFat : NavDestinations

    @Serializable
    data object BodyTemperature : NavDestinations

    @Serializable
    data object CyclingPedalingCadence : NavDestinations

    @Serializable
    data object Exercise : NavDestinations

    @Serializable
    data object HeartRate : NavDestinations

    @Serializable
    data object Height : NavDestinations

    @Serializable
    data object LeanBodyMass : NavDestinations

    @Serializable
    data object MenstruationFlow : NavDestinations

    @Serializable
    data object MenstruationPeriod : NavDestinations

    @Serializable
    data object OvulationTest : NavDestinations

    @Serializable
    data object Power : NavDestinations

    @Serializable
    data object SexualActivity : NavDestinations

    @Serializable
    data object Sleep : NavDestinations

    @Serializable
    data object Steps : NavDestinations

    @Serializable
    data object Weight : NavDestinations

}