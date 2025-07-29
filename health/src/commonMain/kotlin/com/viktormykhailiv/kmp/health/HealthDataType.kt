package com.viktormykhailiv.kmp.health

sealed interface HealthDataType {

    data object BloodGlucose : HealthDataType

    data object BloodPressure : HealthDataType

    data object BodyFat : HealthDataType

    data object BodyTemperature : HealthDataType

    data object HeartRate : HealthDataType

    data object Height : HealthDataType

    data object LeanBodyMass : HealthDataType

    data object Sleep : HealthDataType

    data object Steps : HealthDataType

    data object Weight : HealthDataType
}