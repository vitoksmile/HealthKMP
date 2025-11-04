package com.viktormykhailiv.kmp.health.dataType

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.aggregate.HeightAggregatedRecord
import com.viktormykhailiv.kmp.health.dataType.base.DataTypeTextFieldScreen
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.HeightRecord
import com.viktormykhailiv.kmp.health.units.Length
import com.viktormykhailiv.kmp.health.units.meters
import kotlin.random.Random
import kotlin.time.Clock

@Composable
fun HeightScreen() {
    DataTypeTextFieldScreen(
        title = "Height, cm",
        type = HealthDataType.Height,
        initialValue = { Random.nextInt(150, 200) },
        serializer = { it.toString() },
        deserializer = { it.toIntOrNull() ?: 0 },
        writer = { height ->
            listOf(
                HeightRecord(
                    time = Clock.System.now(),
                    height = Length.meters(height / 100.0),
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        aggregatedContent = { record: HeightAggregatedRecord ->
            Text("Average ${record.avg}")
            Text("Min ${record.min}")
            Text("Max ${record.max}")
        },
        listContent = { height ->
            val average = height.map { it.height.inMeters }.average().meters
            val min = height.minOfOrNull { it.height.inMeters }?.meters
            val max = height.maxOfOrNull { it.height.inMeters }?.meters
            Text("Average $average")
            Text("Min $min")
            Text("Max $max")
        },
    )
}
