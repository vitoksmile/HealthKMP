package com.viktormykhailiv.kmp.health.legacy

import android.content.Context
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.Device
import com.google.android.gms.fitness.data.Field
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.HealthRecord
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.units.Mass
import kotlinx.datetime.Instant
import java.util.concurrent.TimeUnit

internal fun DataPoint.toHealthRecord(type: HealthDataType): HealthRecord {
    val dataPoint = this

    return when (type) {
        Steps -> {
            StepsRecord(
                startTime = Instant.fromEpochMilliseconds(dataPoint.getStartTime(TimeUnit.MILLISECONDS)),
                endTime = Instant.fromEpochMilliseconds(dataPoint.getStartTime(TimeUnit.MILLISECONDS)),
                count = dataPoint.getValue(Field.FIELD_STEPS).asInt(),
            )
        }

        Weight -> {
            WeightRecord(
                time = Instant.fromEpochMilliseconds(dataPoint.getStartTime(TimeUnit.MILLISECONDS)),
                weight = Mass.kilograms(
                    dataPoint.getValue(Field.FIELD_WEIGHT).asFloat().toDouble()
                ),
            )
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

            val dataPoints = recordsByType.mapNotNull { it.toDataPoint(dataSource) }

            DataSet.builder(dataSource)
                .addAll(dataPoints)
                .build()
        }
}

private fun HealthRecord.toDataPoint(
    dataSource: DataSource,
): DataPoint? = when (val record = this) {
    is StepsRecord -> {
        DataPoint.builder(dataSource)
            .setTimeInterval(
                record.startTime.toEpochMilliseconds(),
                record.endTime.toEpochMilliseconds(),
                TimeUnit.MILLISECONDS,
            )
            .setField(Field.FIELD_STEPS, record.count)
            .build()
    }

    is WeightRecord -> {
        DataPoint.builder(dataSource)
            .setTimestamp(
                record.time.toEpochMilliseconds(),
                TimeUnit.MILLISECONDS,
            )
            .setField(Field.FIELD_WEIGHT, record.weight.inKilograms.toFloat())
            .build()
    }

    else -> null
}