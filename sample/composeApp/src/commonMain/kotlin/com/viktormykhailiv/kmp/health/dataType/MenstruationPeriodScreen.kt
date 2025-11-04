package com.viktormykhailiv.kmp.health.dataType

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.dataType.base.DataTypeScreen
import com.viktormykhailiv.kmp.health.duration
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.MenstruationPeriodRecord
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

@Composable
fun MenstruationPeriodScreen() {
    DataTypeScreen(
        title = "Menstruation period",
        type = HealthDataType.MenstruationPeriod,
        initialValue = {},
        writer = { height ->
            listOf(
                MenstruationPeriodRecord(
                    startTime = Clock.System.now()
                        .minus(5.days),
                    endTime = Clock.System.now().minus(1.days),
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        pickerContent = {},
        listContent = { results ->
            if (results.isEmpty()) {
                Text("No periods yet")
            }

            results.forEach {
                Text("Menstruation period ${it.duration}")
            }
        },
    )
}
