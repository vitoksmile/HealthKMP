package com.viktormykhailiv.kmp.health.dataType

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.aggregate.BloodGlucoseAggregatedRecord
import com.viktormykhailiv.kmp.health.dataType.base.DataTypeTextFieldScreen
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.BloodGlucoseRecord
import com.viktormykhailiv.kmp.health.records.MealType
import com.viktormykhailiv.kmp.health.units.BloodGlucose as BloodGlucoseUnit
import kotlin.random.Random
import kotlin.time.Clock

@Composable
fun BloodGlucoseScreen() {
    DataTypeTextFieldScreen(
        title = "Blood glucose",
        type = HealthDataType.BloodGlucose,
        initialValue = { Random.nextInt(20, 40) },
        serializer = { it.toString() },
        deserializer = { it.toIntOrNull() ?: 0 },
        writer = { bloodGlucose ->
            listOf(
                BloodGlucoseRecord(
                    time = Clock.System.now(),
                    level = BloodGlucoseUnit.millimolesPerLiter(bloodGlucose.toDouble()),
                    specimenSource = BloodGlucoseRecord.SpecimenSource.entries.random(),
                    mealType = MealType.entries.random(),
                    relationToMeal = BloodGlucoseRecord.RelationToMeal.entries.random(),
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        aggregatedContent = { record: BloodGlucoseAggregatedRecord ->
            Text("Average ${record.avg}")
            Text("Min ${record.min}")
            Text("Max ${record.max}")
        },
        listContent = { glucose ->
            val average = glucose.map { it.level.inMillimolesPerLiter }
                .average()
                .let { BloodGlucoseUnit.millimolesPerLiter(it) }
            val min = glucose.minOfOrNull { it.level.inMillimolesPerLiter }
                ?.let { BloodGlucoseUnit.millimolesPerLiter(it) }
            val max = glucose.maxOfOrNull { it.level.inMillimolesPerLiter }
                ?.let { BloodGlucoseUnit.millimolesPerLiter(it) }
            Text("Average $average")
            Text("Min $min")
            Text("Max $max")
        },
    )
}
