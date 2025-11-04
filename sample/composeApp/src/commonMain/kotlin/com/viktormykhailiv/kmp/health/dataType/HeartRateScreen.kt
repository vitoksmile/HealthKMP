package com.viktormykhailiv.kmp.health.dataType

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.aggregate.HeartRateAggregatedRecord
import com.viktormykhailiv.kmp.health.dataType.base.DataTypeScreen
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

@Composable
fun HeartRateScreen() {
    DataTypeScreen(
        title = "Heart rate",
        type = HealthDataType.HeartRate,
        initialValue = {},
        writer = {
            val samplesCount = 6
            val sampleInterval = 10.minutes
            val endTime = Clock.System.now()
            val startTime = endTime.minus(sampleInterval * samplesCount)

            listOf(
                HeartRateRecord(
                    startTime = startTime,
                    endTime = endTime,
                    samples = List(samplesCount) {
                        HeartRateRecord.Sample(
                            time = startTime.plus((it * sampleInterval.inWholeMinutes).minutes),
                            beatsPerMinute = Random.nextInt(40, 300),
                        )
                    },
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        pickerContent = {},
        aggregatedContent = { record: HeartRateAggregatedRecord ->
            Text("Average ${record.avg}")
            Text("Min ${record.min}")
            Text("Max ${record.max}")
        },
        listContent = { records ->
            val samples = records.flatMap { it.samples }
            val average = samples.map { it.beatsPerMinute }.average()
            val min = samples.minOfOrNull { it.beatsPerMinute }
            val max = samples.maxOfOrNull { it.beatsPerMinute }
            Text("Average $average")
            Text("Min $min")
            Text("Max $max")
        },
    )
}
