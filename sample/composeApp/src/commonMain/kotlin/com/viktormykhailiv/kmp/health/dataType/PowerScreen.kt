package com.viktormykhailiv.kmp.health.dataType

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.aggregate.PowerAggregatedRecord
import com.viktormykhailiv.kmp.health.dataType.base.DataTypeScreen
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.PowerRecord
import com.viktormykhailiv.kmp.health.units.watts
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

@Composable
fun PowerScreen() {
    DataTypeScreen(
        title = "Power",
        type = HealthDataType.Power,
        initialValue = {},
        writer = {
            val samplesCount = 6
            val sampleInterval = 10.minutes
            val endTime = Clock.System.now()
            val startTime = endTime.minus(sampleInterval * samplesCount)

            listOf(
                PowerRecord(
                    startTime = startTime,
                    endTime = endTime,
                    samples = List(samplesCount) {
                        PowerRecord.Sample(
                            time = startTime.plus((it * sampleInterval.inWholeMinutes).minutes),
                            power = Random.nextDouble(50.0, 300.0).watts,
                        )
                    },
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        pickerContent = {},
        aggregatedContent = { record: PowerAggregatedRecord ->
            Text("Average ${record.avg}")
            Text("Min ${record.min}")
            Text("Max ${record.max}")
        },
        listContent = { records ->
            val samples = records.flatMap { it.samples }
            val average = samples.map { it.power.inWatts }.average().watts
            val min = samples.minOfOrNull { it.power }
            val max = samples.maxOfOrNull { it.power }
            Text("Average $average")
            Text("Min $min")
            Text("Max $max")
        },
    )
}
