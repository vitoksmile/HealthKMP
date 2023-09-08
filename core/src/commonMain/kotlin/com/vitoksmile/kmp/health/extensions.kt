@file:Suppress("unused")

package com.vitoksmile.kmp.health

import com.vitoksmile.kmp.health.HealthDataType.Steps
import com.vitoksmile.kmp.health.records.StepsRecord
import com.vitoksmile.kmp.health.records.WeightRecord
import kotlinx.datetime.Instant

suspend fun HealthManager.readSteps(
    startTime: Instant,
    endTime: Instant,
): Result<List<StepsRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = Steps,
    ).map { it.filterIsInstance<StepsRecord>() }

suspend fun HealthManager.readWeight(
    startTime: Instant,
    endTime: Instant,
): Result<List<WeightRecord>> =
    readData(
        startTime = startTime,
        endTime = endTime,
        type = HealthDataType.Weight,
    ).map { it.filterIsInstance<WeightRecord>() }
