package com.viktormykhailiv.kmp.health.legacy

import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.viktormykhailiv.kmp.health.HealthDataType
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