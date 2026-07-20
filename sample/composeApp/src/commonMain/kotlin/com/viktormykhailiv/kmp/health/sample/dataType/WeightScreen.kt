package com.viktormykhailiv.kmp.health.sample.dataType

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.aggregate.WeightAggregatedRecord
import com.viktormykhailiv.kmp.health.sample.dataType.base.DataTypeTextFieldScreen
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.units.Mass
import com.viktormykhailiv.kmp.health.units.kilograms
import kotlin.random.Random
import kotlin.time.Clock

@Composable
fun WeightScreen() {
    DataTypeTextFieldScreen(
        title = "Weight, kg",
        type = HealthDataType.Weight,
        initialValue = { Random.nextInt(50, 100).toDouble() },
        serializer = { it.toString() },
        deserializer = { it.toDoubleOrNull() ?: 0.0 },
        writer = { weight ->
            listOf(
                WeightRecord(
                    time = Clock.System.now(),
                    weight = Mass.kilograms(weight),
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        aggregatedContent = { record: WeightAggregatedRecord ->
            Text("Average ${record.avg}")
            Text("Min ${record.min}")
            Text("Max ${record.max}")
        },
        groupedAggregatedContent = { records ->
            Text("Grouped Aggregate divided the aggregate data into ${records.size} slices")
            Text("A slice has an average of: ${records.sumOf { it.avg.inKilograms } / records.size} kilograms")
            Text("The slices have a total average of: ${records.sumOf { it.avg.inKilograms }} kilograms")

            Text("Min ${records.minOfOrNull { it.min }}")
            Text("Max ${records.maxOfOrNull { it.max }}")
        },
        listContent = { weight ->
            val average = weight.map { it.weight.inKilograms }.average().kilograms
            val min = weight.minOfOrNull { it.weight.inKilograms }?.kilograms
            val max = weight.maxOfOrNull { it.weight.inKilograms }?.kilograms
            Text("Average $average")
            Text("Min $min")
            Text("Max $max")
        },
    )
}
