package com.viktormykhailiv.kmp.health.legacy

import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.BloodGlucose
import com.viktormykhailiv.kmp.health.HealthDataType.BloodPressure
import com.viktormykhailiv.kmp.health.HealthDataType.BodyFat
import com.viktormykhailiv.kmp.health.HealthDataType.BodyTemperature
import com.viktormykhailiv.kmp.health.HealthDataType.CyclingPedalingCadence
import com.viktormykhailiv.kmp.health.HealthDataType.Exercise
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Height
import com.viktormykhailiv.kmp.health.HealthDataType.LeanBodyMass
import com.viktormykhailiv.kmp.health.HealthDataType.MenstruationFlow
import com.viktormykhailiv.kmp.health.HealthDataType.MenstruationPeriod
import com.viktormykhailiv.kmp.health.HealthDataType.OvulationTest
import com.viktormykhailiv.kmp.health.HealthDataType.Power
import com.viktormykhailiv.kmp.health.HealthDataType.SexualActivity
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight

internal fun fitnessOptions(
    readTypes: List<HealthDataType>,
    writeTypes: List<HealthDataType>,
): FitnessOptions {
    val options = FitnessOptions.builder()

    readTypes.map {
        it.toDataType(isRead = true)
    }.forEach { (type, access) ->
        options.addDataType(type, access)
    }
    writeTypes.map {
        it.toDataType(isWrite = true)
    }.forEach { (type, access) ->
        options.addDataType(type, access)
    }

    return options.build()
}

internal fun HealthDataType.toDataType(): DataType = when (this) {
    is BloodGlucose -> throw IllegalArgumentException("BloodGlucose is not supported")

    is BloodPressure -> throw IllegalArgumentException("BloodPressure is not supported")

    is BodyFat -> DataType.TYPE_BODY_FAT_PERCENTAGE

    is BodyTemperature -> throw IllegalArgumentException("BodyTemperature is not supported")

    is CyclingPedalingCadence -> throw IllegalArgumentException("PedalingCadence is not supported")

    is Exercise -> throw IllegalArgumentException("Exercise is not supported")

    is HeartRate -> DataType.TYPE_HEART_RATE_BPM

    is Height -> DataType.TYPE_HEIGHT

    is LeanBodyMass -> throw IllegalArgumentException("LeanBodyMass is not supported")

    is MenstruationFlow -> throw IllegalArgumentException("MenstruationFlow is not supported")

    is MenstruationPeriod -> throw IllegalArgumentException("MenstruationPeriod is not supported")

    is OvulationTest -> throw IllegalArgumentException("OvulationTest is not supported")

    is Power -> throw IllegalArgumentException("Power is not supported")

    is SexualActivity -> throw IllegalArgumentException("SexualActivity is not supported")

    is Sleep -> DataType.TYPE_SLEEP_SEGMENT

    is Steps -> DataType.TYPE_STEP_COUNT_DELTA

    is Weight -> DataType.TYPE_WEIGHT
}

/**
 * second value => access type
 *
 * @see [FitnessOptions.ACCESS_READ], [FitnessOptions.ACCESS_WRITE]
 */
private fun HealthDataType.toDataType(
    isRead: Boolean = false,
    isWrite: Boolean = false,
): Pair<DataType, Int> {
    require(isRead != isWrite)

    return Pair(
        first = toDataType(),
        second = if (isRead) FitnessOptions.ACCESS_READ else FitnessOptions.ACCESS_WRITE,
    )
}