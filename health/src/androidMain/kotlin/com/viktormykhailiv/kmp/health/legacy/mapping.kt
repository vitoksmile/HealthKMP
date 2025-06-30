package com.viktormykhailiv.kmp.health.legacy

import android.content.Context
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.Device as FitnessDevice
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.data.SleepStages
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Height
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.HealthRecord
import com.viktormykhailiv.kmp.health.groupByRecords
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.HeightRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.records.metadata.Device
import com.viktormykhailiv.kmp.health.records.metadata.DeviceType
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.records.metadata.getLocalDevice
import com.viktormykhailiv.kmp.health.units.Length
import com.viktormykhailiv.kmp.health.units.Mass
import kotlinx.datetime.Instant
import java.util.concurrent.TimeUnit

internal fun List<DataPoint>.toHealthRecords(type: HealthDataType): List<HealthRecord> {
    return when (type) {
        is HeartRate -> {
            map { dataPoint ->
                HeartRateRecord(
                    startTime = dataPoint.startTime,
                    endTime = dataPoint.endTime,
                    samples = listOf(
                        HeartRateRecord.Sample(
                            time = dataPoint.startTime,
                            beatsPerMinute = dataPoint.getValue(Field.FIELD_BPM).asInt(),
                        )
                    ),
                    metadata = dataPoint.toMetadata(),
                )
            }
        }

        is Height -> {
            map { dataPoint ->
                HeightRecord(
                    time = dataPoint.startTime,
                    height = Length.meters(
                        dataPoint.getValue(Field.FIELD_HEIGHT).asFloat().toDouble(),
                    ),
                    metadata = dataPoint.toMetadata(),
                )
            }
        }

        is Sleep -> {
            val metadata = firstOrNull().toMetadata()
            map { dataPoint ->
                val startTime = dataPoint.startTime
                val endTime = dataPoint.endTime
                val stageType = when (dataPoint.getValue(Field.FIELD_SLEEP_SEGMENT_TYPE).asInt()) {
                    SleepStages.AWAKE -> SleepStageType.Awake
                    SleepStages.SLEEP -> SleepStageType.Light
                    SleepStages.OUT_OF_BED -> SleepStageType.OutOfBed
                    SleepStages.SLEEP_LIGHT -> SleepStageType.Light
                    SleepStages.SLEEP_DEEP -> SleepStageType.Deep
                    SleepStages.SLEEP_REM -> SleepStageType.REM
                    else -> SleepStageType.Unknown
                }
                SleepSessionRecord.Stage(
                    startTime = startTime,
                    endTime = endTime,
                    type = stageType,
                )
            }.groupByRecords(metadata)
        }

        is Steps -> {
            map { dataPoint ->
                StepsRecord(
                    startTime = dataPoint.startTime,
                    endTime = dataPoint.endTime,
                    count = dataPoint.getValue(Field.FIELD_STEPS).asInt(),
                    metadata = dataPoint.toMetadata(),
                )
            }
        }

        is Weight -> {
            map { dataPoint ->
                WeightRecord(
                    time = dataPoint.startTime,
                    weight = Mass.kilograms(
                        dataPoint.getValue(Field.FIELD_WEIGHT).asFloat().toDouble(),
                    ),
                    metadata = dataPoint.toMetadata(),
                )
            }
        }
    }
}

internal fun List<HealthRecord>.toDataSets(context: Context): List<DataSet> {
    val records = this

    return records.groupBy { it::class }.values
        .map { recordsByType ->
            val dataSource = DataSource.Builder()
                .setDataType(recordsByType.first().dataType.toDataType())
                .setType(DataSource.TYPE_RAW)
                .setDevice(Device.getLocalDevice().toFitnessDevice())
                .setAppPackageName(context.applicationContext)
                .build()

            val dataPoints = recordsByType.map { it.toDataPoints(dataSource) }.flatten()

            DataSet.builder(dataSource)
                .addAll(dataPoints)
                .build()
        }
}

private fun HealthRecord.toDataPoints(
    dataSource: DataSource,
): List<DataPoint> = when (val record = this) {
    is HeartRateRecord -> {
        record.samples.map { sample ->
            DataPoint.builder(dataSource)
                .setTimeInterval(
                    record.startTime.toEpochMilliseconds(),
                    record.endTime.toEpochMilliseconds(),
                    TimeUnit.MILLISECONDS,
                )
                .setField(Field.FIELD_BPM, sample.beatsPerMinute)
                .build()
        }
    }

    is HeightRecord -> {
        listOf(
            DataPoint.builder(dataSource)
                .setTimestamp(
                    record.time.toEpochMilliseconds(),
                    TimeUnit.MILLISECONDS,
                )
                .setField(Field.FIELD_HEIGHT, record.height.inMeters.toFloat())
                .build()
        )
    }

    is SleepSessionRecord -> {
        record.stages.map { stage ->
            val type = when (stage.type) {
                SleepStageType.Awake -> SleepStages.AWAKE
                SleepStageType.OutOfBed -> SleepStages.OUT_OF_BED
                SleepStageType.Light -> SleepStages.SLEEP_LIGHT
                SleepStageType.Deep -> SleepStages.SLEEP_DEEP
                SleepStageType.REM -> SleepStages.SLEEP_REM
                SleepStageType.AwakeInBed -> SleepStages.AWAKE
                SleepStageType.Sleeping -> SleepStages.SLEEP
                SleepStageType.Unknown -> SleepStages.SLEEP
            }
            DataPoint.builder(dataSource)
                .setTimeInterval(
                    record.startTime.toEpochMilliseconds(),
                    record.endTime.toEpochMilliseconds(),
                    TimeUnit.MILLISECONDS,
                )
                .setField(Field.FIELD_SLEEP_SEGMENT_TYPE, type)
                .build()
        }
    }

    is StepsRecord -> {
        listOf(
            DataPoint.builder(dataSource)
                .setTimeInterval(
                    record.startTime.toEpochMilliseconds(),
                    record.endTime.toEpochMilliseconds(),
                    TimeUnit.MILLISECONDS,
                )
                .setField(Field.FIELD_STEPS, record.count)
                .build()
        )
    }

    is WeightRecord -> {
        listOf(
            DataPoint.builder(dataSource)
                .setTimestamp(
                    record.time.toEpochMilliseconds(),
                    TimeUnit.MILLISECONDS,
                )
                .setField(Field.FIELD_WEIGHT, record.weight.inKilograms.toFloat())
                .build()
        )
    }

    else -> emptyList()
}

private inline val DataPoint.startTime: Instant
    get() = Instant.fromEpochMilliseconds(getStartTime(TimeUnit.MILLISECONDS))

private inline val DataPoint.endTime: Instant
    get() = Instant.fromEpochMilliseconds(getEndTime(TimeUnit.MILLISECONDS))

private fun DataPoint?.toMetadata(): Metadata = Metadata.unknownRecordingMethod(
    device = this?.dataSource?.device?.toDevice(),
)

private fun FitnessDevice.toDevice(): Device = Device(
    type = when (type) {
        FitnessDevice.TYPE_PHONE -> DeviceType.Phone
        FitnessDevice.TYPE_TABLET -> DeviceType.Phone
        FitnessDevice.TYPE_WATCH -> DeviceType.Watch
        FitnessDevice.TYPE_CHEST_STRAP -> DeviceType.ChestStrap
        FitnessDevice.TYPE_SCALE -> DeviceType.Scale
        FitnessDevice.TYPE_HEAD_MOUNTED -> DeviceType.HeadMounted
        else -> DeviceType.Unknown
    },
    manufacturer = manufacturer,
    model = model,
)

private fun Device.toFitnessDevice(): FitnessDevice = FitnessDevice(
    /* manufacturer = */ manufacturer ?: "unknown",
    /* model = */ model ?: "unknown",
    /* uid = */ "unknown",
    /* type = */
    when (type) {
        DeviceType.Phone -> FitnessDevice.TYPE_PHONE
        DeviceType.Watch -> FitnessDevice.TYPE_WATCH
        DeviceType.ChestStrap -> FitnessDevice.TYPE_CHEST_STRAP
        DeviceType.Scale -> FitnessDevice.TYPE_SCALE
        DeviceType.HeadMounted -> FitnessDevice.TYPE_HEAD_MOUNTED
        else -> FitnessDevice.TYPE_UNKNOWN
    },
)
