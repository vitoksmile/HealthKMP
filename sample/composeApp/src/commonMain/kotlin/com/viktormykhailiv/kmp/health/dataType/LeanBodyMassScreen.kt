package com.viktormykhailiv.kmp.health.dataType

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.aggregate.LeanBodyMassAggregatedRecord
import com.viktormykhailiv.kmp.health.dataType.base.DataTypeTextFieldScreen
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.LeanBodyMassRecord
import com.viktormykhailiv.kmp.health.units.Mass
import com.viktormykhailiv.kmp.health.units.kilograms
import kotlin.random.Random
import kotlin.time.Clock

@Composable
fun LeanBodyMassScreen() {
    DataTypeTextFieldScreen(
        title = "Lean body mass",
        type = HealthDataType.LeanBodyMass,
        initialValue = { Random.nextInt(30, 60).toDouble() },
        serializer = { it.toString() },
        deserializer = { it.toDoubleOrNull() ?: 0.0 },
        writer = { leanBodyMass ->
            listOf(
                LeanBodyMassRecord(
                    time = Clock.System.now(),
                    mass = Mass.kilograms(leanBodyMass),
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        aggregatedContent = { record: LeanBodyMassAggregatedRecord ->
            Text("Average ${record.avg}")
            Text("Min ${record.min}")
            Text("Max ${record.max}")
        },
        listContent = { mass ->
            val average = mass.map { it.mass.inKilograms }.average().kilograms
            val min = mass.minOfOrNull { it.mass.inKilograms }?.kilograms
            val max = mass.maxOfOrNull { it.mass.inKilograms }?.kilograms
            Text("Average $average")
            Text("Min $min")
            Text("Max $max")
        },
    )
}
