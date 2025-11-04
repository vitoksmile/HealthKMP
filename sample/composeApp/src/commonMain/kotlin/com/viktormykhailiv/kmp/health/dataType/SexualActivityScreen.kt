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
import com.viktormykhailiv.kmp.health.records.SexualActivityRecord
import kotlin.time.Clock

@Composable
fun SexualActivityScreen() {
    val protections = remember {
        listOf(
            SexualActivityRecord.Protection.Unknown,
            SexualActivityRecord.Protection.Protected,
            SexualActivityRecord.Protection.Unprotected,
        )
    }

    DataTypeScreen(
        title = "Ovulation test",
        type = HealthDataType.SexualActivity,
        initialValue = { protections.random() },
        writer = { protection ->
            listOf(
                SexualActivityRecord(
                    time = Clock.System.now(),
                    protection = protection,
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        pickerContent = { controller ->
            ValuesPicker(
                modifier = Modifier.fillMaxWidth(),
                values = protections,
                currentValue = controller.value,
                onChanged = { controller.value = it },
                mapper = { it::class.simpleName.orEmpty() },
            )
        },
        listContent = { records ->
            if (records.isEmpty()) {
                Text("No results yet")
            }

            records.forEach {
                Text("Sexual activity ${it.protection}")
            }
        },
    )
}
