package com.viktormykhailiv.kmp.health.dataType

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.aggregate.StepsAggregatedRecord
import com.viktormykhailiv.kmp.health.dataType.base.DataTypeTextFieldScreen
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.StepsRecord
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

@Composable
fun StepsScreen() {
    DataTypeTextFieldScreen(
        title = "Steps",
        type = HealthDataType.Steps,
        initialValue = { Random.nextInt(1, 100) },
        serializer = { it.toString() },
        deserializer = { it.toIntOrNull() ?: 0 },
        writer = { steps ->
            listOf(
                StepsRecord(
                    startTime = Clock.System.now()
                        .minus(1.hours),
                    endTime = Clock.System.now(),
                    count = steps,
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        aggregatedContent = { record: StepsAggregatedRecord ->
            Text("Total ${record.count}")
        },
        listContent = { steps ->
            val average = steps.map { it.count }.average()
            val total = steps.sumOf { it.count }
            Text("Average $average")
            Text("Total $total")
        },
    )
}
