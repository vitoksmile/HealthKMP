package com.viktormykhailiv.kmp.health

sealed interface HealthDataType {

    data object BloodGlucose : HealthDataType

    data object BloodPressure : HealthDataType

    data object BodyFat : HealthDataType

    data object BodyTemperature : HealthDataType

    data object CyclingPedalingCadence : HealthDataType

    data class Exercise(
        val activeEnergyBurned: Boolean = true,
        val cyclingPower: Boolean = true,
        val cyclingSpeed: Boolean = true,
        val flightsClimbed: Boolean = true,
        val distanceWalkingRunning: Boolean = true,
        val runningSpeed: Boolean = true,
    ) : HealthDataType

    data object HeartRate : HealthDataType

    data object Height : HealthDataType

    data object LeanBodyMass : HealthDataType

    data object MenstruationFlow : HealthDataType

    data object MenstruationPeriod : HealthDataType

    data object OvulationTest : HealthDataType

    data object Power : HealthDataType

    data object SexualActivity : HealthDataType

    data object Sleep : HealthDataType

    data object Steps : HealthDataType

    data object Weight : HealthDataType
}
