package com.viktormykhailiv.kmp.health.legacy

import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.BloodGlucose
import com.viktormykhailiv.kmp.health.HealthDataType.BloodPressure
import com.viktormykhailiv.kmp.health.HealthDataType.BodyFat
import com.viktormykhailiv.kmp.health.HealthDataType.BodyTemperature
import com.viktormykhailiv.kmp.health.HealthDataType.Exercise
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Height
import com.viktormykhailiv.kmp.health.HealthDataType.LeanBodyMass
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
    BloodGlucose -> throw IllegalArgumentException("BloodGlucose is not supported")

    BloodPressure -> throw IllegalArgumentException("BloodPressure is not supported")

    BodyFat -> DataType.TYPE_BODY_FAT_PERCENTAGE

    BodyTemperature -> throw IllegalArgumentException("BodyTemperature is not supported")

    is Exercise -> throw IllegalArgumentException("Exercise is not supported")

    HeartRate -> DataType.TYPE_HEART_RATE_BPM

    Height -> DataType.TYPE_HEIGHT

    LeanBodyMass -> throw IllegalArgumentException("LeanBodyMass is not supported")

    Sleep -> DataType.TYPE_SLEEP_SEGMENT

    Steps -> DataType.TYPE_STEP_COUNT_DELTA

    Weight -> DataType.TYPE_WEIGHT
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