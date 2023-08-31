package com.vitoksmile.kmp.health

import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord

internal fun HealthDataType.toHealthPermission(
    isRead: Boolean = false,
    isWrite: Boolean = false
): String {
    require(isRead != isWrite)

    return (when (this) {
        HealthDataType.STEPS -> StepsRecord::class
        HealthDataType.WEIGHT -> WeightRecord::class
    }).let {
        if (isRead) {
            HealthPermission.getReadPermission(it)
        } else {
            HealthPermission.getWritePermission(it)
        }
    }
}