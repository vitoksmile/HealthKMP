package com.viktormykhailiv.kmp.health.dataType

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.aggregate.SleepAggregatedRecord
import com.viktormykhailiv.kmp.health.dataType.base.DataTypeScreen
import com.viktormykhailiv.kmp.health.duration
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import com.viktormykhailiv.kmp.health.sleep.SleepSessionCanvas
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun SleepScreen() {
    DataTypeScreen(
        title = "Sleep",
        type = HealthDataType.Sleep,
        initialValue = {},
        writer = {
            val startTime = Clock.System.now()
                .minus(12.hours)
            val endTime = Clock.System.now()
                .minus(11.hours)
            val types = listOf(
                SleepStageType.Awake,
                SleepStageType.OutOfBed,
                SleepStageType.Sleeping,
                SleepStageType.Light,
                SleepStageType.Deep,
                SleepStageType.REM,
            )

            listOf(
                SleepSessionRecord(
                    startTime = startTime,
                    endTime = endTime,
                    stages = List(6) {
                        SleepSessionRecord.Stage(
                            startTime = startTime.plus((10 * it).minutes),
                            endTime = startTime.plus((10 * it).minutes + 10.minutes),
                            type = types[it],
                        )
                    },
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        pickerContent = {},
        aggregatedContent = { record: SleepAggregatedRecord ->
            Text("Total ${record.totalDuration}")
        },
        listContent = { records ->
            if (records.isEmpty()) {
                Text("No sleep yet")
            }

            records.forEach { record ->
                Text("Sleep session duration ${record.duration}")
                SleepSessionCanvas(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 16.dp),
                    record = record,
                )
            }
        },
    )
}
