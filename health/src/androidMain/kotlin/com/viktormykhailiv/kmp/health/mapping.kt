package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.units.Mass
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import androidx.health.connect.client.records.Record as HCRecord
import androidx.health.connect.client.records.HeartRateRecord as HCHeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord as HCSleepSessionRecord
import androidx.health.connect.client.records.StepsRecord as HCStepsRecord
import androidx.health.connect.client.records.WeightRecord as HCWeightRecord
import androidx.health.connect.client.units.Mass as HCMass

internal fun HealthRecord.toHCRecord(): HCRecord? = when (val record = this) {
    is HeartRateRecord -> HCHeartRateRecord(
        startTime = record.startTime.toJavaInstant(),
        endTime = record.endTime.toJavaInstant(),
        startZoneOffset = null,
        endZoneOffset = null,
        samples = record.samples.map { sample ->
            HCHeartRateRecord.Sample(
                time = sample.time.toJavaInstant(),
                beatsPerMinute = sample.beatsPerMinute.toLong(),
            )
        },
    )

    is SleepSessionRecord -> HCSleepSessionRecord(
        startTime = record.startTime.toJavaInstant(),
        endTime = record.endTime.toJavaInstant(),
        startZoneOffset = null,
        endZoneOffset = null,
        stages = record.stages.map { stage ->
            HCSleepSessionRecord.Stage(
                startTime = stage.startTime.toJavaInstant(),
                endTime = stage.endTime.toJavaInstant(),
                stage = when (stage.type) {
                    SleepStageType.Unknown -> HCSleepSessionRecord.STAGE_TYPE_UNKNOWN
                    SleepStageType.Awake -> HCSleepSessionRecord.STAGE_TYPE_AWAKE
                    SleepStageType.AwakeInBed -> HCSleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED
                    SleepStageType.Sleeping -> HCSleepSessionRecord.STAGE_TYPE_SLEEPING
                    SleepStageType.OutOfBed -> HCSleepSessionRecord.STAGE_TYPE_OUT_OF_BED
                    SleepStageType.Light -> HCSleepSessionRecord.STAGE_TYPE_LIGHT
                    SleepStageType.Deep -> HCSleepSessionRecord.STAGE_TYPE_DEEP
                    SleepStageType.REM -> HCSleepSessionRecord.STAGE_TYPE_REM
                },
            )
        },
    )

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
    is HCHeartRateRecord -> HeartRateRecord(
        startTime = record.startTime.toKotlinInstant(),
        endTime = record.endTime.toKotlinInstant(),
        samples = record.samples.map { sample ->
            HeartRateRecord.Sample(
                time = sample.time.toKotlinInstant(),
                beatsPerMinute = sample.beatsPerMinute.toInt(),
            )
        },
    )

    is HCSleepSessionRecord -> SleepSessionRecord(
        startTime = record.startTime.toKotlinInstant(),
        endTime = record.endTime.toKotlinInstant(),
        stages = record.stages.map { stage ->
            SleepSessionRecord.Stage(
                startTime = stage.startTime.toKotlinInstant(),
                endTime = stage.endTime.toKotlinInstant(),
                type = when (stage.stage) {
                    HCSleepSessionRecord.STAGE_TYPE_AWAKE -> SleepStageType.Awake
                    HCSleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED -> SleepStageType.AwakeInBed
                    HCSleepSessionRecord.STAGE_TYPE_SLEEPING -> SleepStageType.Sleeping
                    HCSleepSessionRecord.STAGE_TYPE_OUT_OF_BED -> SleepStageType.OutOfBed
                    HCSleepSessionRecord.STAGE_TYPE_LIGHT -> SleepStageType.Light
                    HCSleepSessionRecord.STAGE_TYPE_DEEP -> SleepStageType.Deep
                    HCSleepSessionRecord.STAGE_TYPE_REM -> SleepStageType.REM
                    else -> SleepStageType.Unknown
                }
            )
        }
    )

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

internal fun HCMass.toMass(): Mass =
    Mass.kilograms(inKilograms)
