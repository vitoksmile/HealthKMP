package com.viktormykhailiv.kmp.health.dataType

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.aggregate.BloodPressureAggregatedRecord
import com.viktormykhailiv.kmp.health.dataType.base.DataTypeScreen
import com.viktormykhailiv.kmp.health.dataType.base.DataTypeScreenDefaults
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.BloodPressureRecord
import com.viktormykhailiv.kmp.health.units.millimetersOfMercury
import kotlin.random.Random
import kotlin.time.Clock

@Composable
fun BloodPressureScreen() {
    DataTypeScreen(
        title = "Blood pressure",
        type = HealthDataType.BloodPressure,
        initialValue = {
            BloodPressureState(
                systolic = Random.nextInt(100, 140),
                diastolic = Random.nextInt(70, 90),
            )
        },
        writer = { bloodPressure ->
            listOf(
                BloodPressureRecord(
                    time = Clock.System.now(),
                    systolic = bloodPressure.systolic.millimetersOfMercury,
                    diastolic = bloodPressure.diastolic.millimetersOfMercury,
                    bodyPosition = null,
                    measurementLocation = null,
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        pickerContent = { controller ->
            DataTypeScreenDefaults.TextField(
                controller = controller,
                title = "Systolic blood pressure",
                serializer = { it.systolic.toString() },
                deserializer = { controller.value.copy(systolic = it.toIntOrNull() ?: 0) },
            )

            DataTypeScreenDefaults.TextField(
                controller = controller,
                title = "Diastolic blood pressure",
                serializer = { it.diastolic.toString() },
                deserializer = { controller.value.copy(diastolic = it.toIntOrNull() ?: 0) },
            )
        },
        aggregatedContent = { record: BloodPressureAggregatedRecord ->
            Text("Average ${record.systolic.avg}/${record.diastolic.avg}")
            Text("Min ${record.systolic.min}/${record.diastolic.min}")
            Text("Max ${record.systolic.max}/${record.diastolic.max}")
        },
        listContent = { bloodPressure ->
            val systolicAvg = bloodPressure.map { it.systolic.inMillimetersOfMercury }.average()
            val systolicMin = bloodPressure.minOfOrNull { it.systolic.inMillimetersOfMercury }
            val systolicMax = bloodPressure.maxOfOrNull { it.systolic.inMillimetersOfMercury }
            val diastolicAvg = bloodPressure.map { it.diastolic.inMillimetersOfMercury }.average()
            val diastolicMin = bloodPressure.minOfOrNull { it.diastolic.inMillimetersOfMercury }
            val diastolicMax = bloodPressure.maxOfOrNull { it.diastolic.inMillimetersOfMercury }
            Text("Average $systolicAvg/$diastolicAvg")
            Text("Min $systolicMin/$diastolicMin")
            Text("Max $systolicMax/$diastolicMax")
        },
    )
}

private data class BloodPressureState(
    val systolic: Int,
    val diastolic: Int,
)
