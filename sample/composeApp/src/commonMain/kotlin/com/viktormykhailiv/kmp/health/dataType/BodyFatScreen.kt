package com.viktormykhailiv.kmp.health.dataType

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.aggregate.BodyFatAggregatedRecord
import com.viktormykhailiv.kmp.health.dataType.base.DataTypeTextFieldScreen
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.BodyFatRecord
import com.viktormykhailiv.kmp.health.units.percent
import kotlin.random.Random
import kotlin.time.Clock

@Composable
fun BodyFatScreen() {
    DataTypeTextFieldScreen(
        title = "Body fat",
        type = HealthDataType.BodyFat,
        initialValue = { Random.nextInt(1, 100) },
        serializer = { it.toString() },
        deserializer = { it.toIntOrNull() ?: 0 },
        writer = { bodyFat ->
            listOf(
                BodyFatRecord(
                    time = Clock.System.now(),
                    percentage = bodyFat.percent,
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        aggregatedContent = { record: BodyFatAggregatedRecord ->
            Text("Average ${record.avg}")
            Text("Min ${record.min}")
            Text("Max ${record.max}")
        },
        listContent = { fat ->
            val average = fat.map { it.percentage.value }.average().percent
            val min = fat.minOfOrNull { it.percentage.value }?.percent
            val max = fat.maxOfOrNull { it.percentage.value }?.percent
            Text("Average $average")
            Text("Min $min")
            Text("Max $max")
        },
    )
}
