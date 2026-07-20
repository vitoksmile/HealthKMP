package com.viktormykhailiv.kmp.health.sample.dataType

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.aggregate.HeightAggregatedRecord
import com.viktormykhailiv.kmp.health.sample.dataType.base.DataTypeTextFieldScreen
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
        groupedAggregatedContent = { records ->
            Text("Grouped Aggregate divided the aggregate data into ${records.size} slices")
            Text("A slice has an average of: ${records.sumOf { it.avg.inMeters } / records.size} meters")
            Text("The slices have a total average of: ${records.sumOf { it.avg.inMeters }} meters")

            Text("Min ${records.minOfOrNull { it.min }}")
            Text("Max ${records.maxOfOrNull { it.max }}")
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
