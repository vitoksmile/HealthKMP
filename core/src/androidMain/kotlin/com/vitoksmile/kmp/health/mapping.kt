package com.vitoksmile.kmp.health

import com.vitoksmile.kmp.health.records.StepsRecord
import com.vitoksmile.kmp.health.records.WeightRecord
import com.vitoksmile.kmp.health.units.Mass
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import androidx.health.connect.client.records.Record as HCRecord
import androidx.health.connect.client.records.StepsRecord as HCStepsRecord
import androidx.health.connect.client.records.WeightRecord as HCWeightRecord
import androidx.health.connect.client.units.Mass as HCMass

internal fun HealthRecord.toHCRecord(): HCRecord? = when (val record = this) {
    is StepsRecord -> HCStepsRecord(
        startTime = record.startTime.toJavaInstant(),
        endTime = record.endTime.toJavaInstant(),
        startZoneOffset = null,
        endZoneOffset = null,
        count = record.count.toLong(),
    )

    is WeightRecord -> HCWeightRecord(
        time = record.time.toJavaInstant(),
        zoneOffset = null,
        weight = record.weight.toHCMass(),
    )

    else -> null
}

internal fun HCRecord.toHealthRecord(): HealthRecord? = when (val record = this) {
    is HCStepsRecord -> StepsRecord(
        startTime = record.startTime.toKotlinInstant(),
        endTime = record.endTime.toKotlinInstant(),
        count = record.count.toInt(),
    )

    is HCWeightRecord -> WeightRecord(
        time = record.time.toKotlinInstant(),
        weight = record.weight.toMass(),
    )

    else -> null
}

private fun Mass.toHCMass(): HCMass =
    HCMass.kilograms(inKilograms)

private fun HCMass.toMass(): Mass =
    Mass.kilograms(inKilograms)
