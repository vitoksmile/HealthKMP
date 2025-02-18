package com.viktormykhailiv.kmp.health.legacy

import android.content.Context
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.Device
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.data.SleepStages
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.HealthRecord
import com.viktormykhailiv.kmp.health.groupByRecords
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.units.Mass
import kotlinx.datetime.Instant
import java.util.concurrent.TimeUnit

internal fun List<DataPoint>.toHealthRecords(type: HealthDataType): List<HealthRecord> {
    return when (type) {
        is Sleep -> {
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
            }.groupByRecords()
        }

        is Steps -> {
            map { dataPoint ->
                StepsRecord(
                    startTime = dataPoint.startTime,
                    endTime = dataPoint.endTime,
                    count = dataPoint.getValue(Field.FIELD_STEPS).asInt(),
                )
            }
        }

        is Weight -> {
            map { dataPoint ->
                WeightRecord(
                    time = dataPoint.startTime,
                    weight = Mass.kilograms(
                        dataPoint.getValue(Field.FIELD_WEIGHT).asFloat().toDouble()
                    ),
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
                .setDevice(Device.getLocalDevice(context))
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
    is SleepSessionRecord -> {
        record.stages.map { stage ->
            val type = when (stage.type) {
                SleepStageType.Awake -> SleepStages.AWAKE
                SleepStageType.Light -> SleepStages.SLEEP
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
