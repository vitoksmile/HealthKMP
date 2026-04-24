package com.viktormykhailiv.kmp.health

/**
 * Represents different types of health data supported by the library.
 */
sealed interface HealthDataType {

    /** Blood glucose measurement. */
    data object BloodGlucose : HealthDataType

    /** Blood pressure measurement (systolic and diastolic). */
    data object BloodPressure : HealthDataType

    /** Percentage of body fat. */
    data object BodyFat : HealthDataType

    /** Body temperature measurement. */
    data object BodyTemperature : HealthDataType

    /** Number of pedal cycles per minute during cycling. */
    data object CyclingPedalingCadence : HealthDataType

    /**
     * Exercise session information.
     *
     * @param activeEnergyBurned Whether to track active energy burned.
     * @param cyclingPower Whether to track cycling power.
     * @param cyclingSpeed Whether to track cycling speed.
     * @param flightsClimbed Whether to track flights climbed.
     * @param distanceWalkingRunning Whether to track walking/running distance.
     * @param runningSpeed Whether to track running speed.
     */
    data class Exercise(
        val activeEnergyBurned: Boolean = true,
        val cyclingPower: Boolean = true,
        val cyclingSpeed: Boolean = true,
        val flightsClimbed: Boolean = true,
        val distanceWalkingRunning: Boolean = true,
        val runningSpeed: Boolean = true,
    ) : HealthDataType

    /** Heart rate measurement in beats per minute. */
    data object HeartRate : HealthDataType

    /** Height measurement. */
    data object Height : HealthDataType

    /** Lean body mass measurement. */
    data object LeanBodyMass : HealthDataType

    /** Menstruation flow intensity. */
    data object MenstruationFlow : HealthDataType

    /** Menstruation period date range. */
    data object MenstruationPeriod : HealthDataType

    /** Ovulation test result. */
    data object OvulationTest : HealthDataType

    /** Power measurement (e.g., cycling power). */
    data object Power : HealthDataType

    /** Sexual activity record. */
    data object SexualActivity : HealthDataType

    /** Sleep session information. */
    data object Sleep : HealthDataType

    /** Step count record. */
    data object Steps : HealthDataType

    /** Body weight measurement. */
    data object Weight : HealthDataType
}
