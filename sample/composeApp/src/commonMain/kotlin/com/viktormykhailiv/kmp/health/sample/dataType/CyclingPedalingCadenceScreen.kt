package com.viktormykhailiv.kmp.health.sample.dataType

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.aggregate.CyclingPedalingCadenceAggregatedRecord
import com.viktormykhailiv.kmp.health.sample.dataType.base.DataTypeScreen
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.CyclingPedalingCadenceRecord
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

@Composable
fun CyclingPedalingCadenceScreen() {
    DataTypeScreen(
        title = "Cycling pedaling cadence",
        type = HealthDataType.CyclingPedalingCadence,
        initialValue = {},
        writer = {
            val samplesCount = 6
            val sampleInterval = 10.minutes
            val endTime = Clock.System.now()
            val startTime = endTime.minus(sampleInterval * samplesCount)

            listOf(
                CyclingPedalingCadenceRecord(
                    startTime = startTime,
                    endTime = endTime,
                    samples = List(samplesCount) {
                        CyclingPedalingCadenceRecord.Sample(
                            time = startTime.plus((it * sampleInterval.inWholeMinutes).minutes),
                            revolutionsPerMinute = Random.nextDouble(10.0, 150.0),
                        )
                    },
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        pickerContent = {},
        aggregatedContent = { record: CyclingPedalingCadenceAggregatedRecord ->
            Text("Average ${record.avg}")
            Text("Min ${record.min}")
            Text("Max ${record.max}")
        },
        groupedAggregatedContent = { records ->
            Text("Grouped Aggregate divided the aggregate data into ${records.size} slices")
            Text("A slice has an average of: ${records.sumOf { it.avg } / records.size} ")
            Text("The slices have a total average of: ${records.sumOf { it.avg }} ")

            Text("Min ${records.minOfOrNull { it.min }}")
            Text("Max ${records.maxOfOrNull { it.max }}")
        },
        listContent = { records ->
            val samples = records.flatMap { it.samples }
            val average = samples.map { it.revolutionsPerMinute }.average()
            val min = samples.minOfOrNull { it.revolutionsPerMinute }
            val max = samples.maxOfOrNull { it.revolutionsPerMinute }
            Text("Average $average")
            Text("Min $min")
            Text("Max $max")
        },
    )
}
