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
import com.viktormykhailiv.kmp.health.records.MenstruationFlowRecord
import kotlin.time.Clock

@Composable
fun MenstruationFlowScreen() {
    val flows = remember {
        listOf(
            MenstruationFlowRecord.Flow.Unknown,
            MenstruationFlowRecord.Flow.Light,
            MenstruationFlowRecord.Flow.Medium,
            MenstruationFlowRecord.Flow.Heavy,
        )
    }

    DataTypeScreen(
        title = "Menstruation flow",
        type = HealthDataType.MenstruationFlow,
        initialValue = { flows.random() },
        writer = { flow ->
            listOf(
                MenstruationFlowRecord(
                    time = Clock.System.now(),
                    flow = flow,
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        pickerContent = { controller ->
            ValuesPicker(
                modifier = Modifier.fillMaxWidth(),
                values = flows,
                currentValue = controller.value,
                onChanged = { controller.value = it },
                mapper = { it::class.simpleName.orEmpty() },
            )
        },
        listContent = { flows ->
            if (flows.isEmpty()) {
                Text("No flows yet")
            }

            flows.forEach {
                Text("Menstruation flow ${it.flow}")
            }
        },
    )
}
