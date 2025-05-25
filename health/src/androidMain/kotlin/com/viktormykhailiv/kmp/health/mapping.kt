package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.records.metadata.Device
import com.viktormykhailiv.kmp.health.records.metadata.DeviceType
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.units.Mass
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import androidx.health.connect.client.records.metadata.Device as HCDevice
import androidx.health.connect.client.records.metadata.Metadata as HCMetadata
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
        metadata = record.metadata.toHCMetadata(),
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
        metadata = record.metadata.toHCMetadata(),
    )

    is StepsRecord -> HCStepsRecord(
        startTime = record.startTime.toJavaInstant(),
        endTime = record.endTime.toJavaInstant(),
        startZoneOffset = null,
        endZoneOffset = null,
        count = record.count.toLong(),
        metadata = record.metadata.toHCMetadata(),
    )

    is WeightRecord -> HCWeightRecord(
        time = record.time.toJavaInstant(),
        zoneOffset = null,
        weight = record.weight.toHCMass(),
        metadata = record.metadata.toHCMetadata(),
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
        metadata = record.metadata.toMetadata(),
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
        },
        metadata = record.metadata.toMetadata(),
    )

    is HCStepsRecord -> StepsRecord(
        startTime = record.startTime.toKotlinInstant(),
        endTime = record.endTime.toKotlinInstant(),
        count = record.count.toInt(),
        metadata = record.metadata.toMetadata(),
    )

    is HCWeightRecord -> WeightRecord(
        time = record.time.toKotlinInstant(),
        weight = record.weight.toMass(),
        metadata = record.metadata.toMetadata(),
    )

    else -> null
}

private fun Metadata.toHCMetadata(): HCMetadata = when (recordingMethod) {
    is Metadata.RecordingMethod.Unknown -> HCMetadata.unknownRecordingMethod(
        device = device?.toHCDevice(),
    )

    is Metadata.RecordingMethod.ManualEntry -> HCMetadata.manualEntry(
        device = device?.toHCDevice(),
    )

    is Metadata.RecordingMethod.AutoRecorded -> device?.let {
        HCMetadata.autoRecorded(device = it.toHCDevice())
    } ?: HCMetadata.unknownRecordingMethod()
}

private fun HCMetadata.toMetadata(): Metadata = when (recordingMethod) {
    HCMetadata.RECORDING_METHOD_MANUAL_ENTRY -> Metadata.manualEntry(
        id = id,
        device = device?.toDevice(),
    )

    HCMetadata.RECORDING_METHOD_AUTOMATICALLY_RECORDED -> device?.let {
        Metadata.autoRecorded(
            id = id,
            device = it.toDevice(),
        )
    } ?: Metadata.unknownRecordingMethod(id = id)

    else -> Metadata.unknownRecordingMethod(
        id = id,
        device = device?.toDevice(),
    )
}

private fun Device.toHCDevice(): HCDevice = HCDevice(
    type = when (type) {
        is DeviceType.Unknown -> HCDevice.TYPE_UNKNOWN
        is DeviceType.Watch -> HCDevice.TYPE_WATCH
        is DeviceType.Phone -> HCDevice.TYPE_PHONE
        is DeviceType.Scale -> HCDevice.TYPE_SCALE
        is DeviceType.Ring -> HCDevice.TYPE_RING
        is DeviceType.HeadMounted -> HCDevice.TYPE_HEAD_MOUNTED
        is DeviceType.FitnessBand -> HCDevice.TYPE_FITNESS_BAND
        is DeviceType.ChestStrap -> HCDevice.TYPE_CHEST_STRAP
        is DeviceType.SmartDisplay -> HCDevice.TYPE_SMART_DISPLAY
    },
    manufacturer = manufacturer,
    model = model,
)

private fun HCDevice.toDevice(): Device = Device(
    type = when (type) {
        HCDevice.TYPE_WATCH -> DeviceType.Watch
        HCDevice.TYPE_PHONE -> DeviceType.Phone
        HCDevice.TYPE_SCALE -> DeviceType.Scale
        HCDevice.TYPE_RING -> DeviceType.Ring
        HCDevice.TYPE_HEAD_MOUNTED -> DeviceType.HeadMounted
        HCDevice.TYPE_FITNESS_BAND -> DeviceType.FitnessBand
        HCDevice.TYPE_CHEST_STRAP -> DeviceType.ChestStrap
        HCDevice.TYPE_SMART_DISPLAY -> DeviceType.SmartDisplay
        else -> DeviceType.Unknown
    },
    manufacturer = manufacturer,
    model = model,
)

private fun Mass.toHCMass(): HCMass = HCMass.kilograms(inKilograms)

internal fun HCMass.toMass(): Mass = Mass.kilograms(inKilograms)
