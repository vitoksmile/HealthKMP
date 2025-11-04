package com.viktormykhailiv.kmp.health.dataType

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.dataType.base.DataTypeScreen
import com.viktormykhailiv.kmp.health.dataType.base.ValuesPicker
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.OvulationTestRecord
import kotlin.time.Clock

@Composable
fun OvulationTestScreen() {
    val results = remember {
        listOf(
            OvulationTestRecord.Result.Inconclusive,
            OvulationTestRecord.Result.Positive,
            OvulationTestRecord.Result.High,
            OvulationTestRecord.Result.Negative,
        )
    }

    DataTypeScreen(
        title = "Ovulation test",
        type = HealthDataType.OvulationTest,
        initialValue = { results.random() },
        writer = { result ->
            listOf(
                OvulationTestRecord(
                    time = Clock.System.now(),
                    result = result,
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        pickerContent = { controller ->
            ValuesPicker(
                modifier = Modifier.fillMaxWidth(),
                values = results,
                currentValue = controller.value,
                onChanged = { controller.value = it },
                mapper = { it::class.simpleName.orEmpty() },
            )
        },
        listContent = { results ->
            if (results.isEmpty()) {
                Text("No results yet")
            }

            results.forEach {
                Text("Ovulation result ${it.result}")
            }
        },
    )
}
