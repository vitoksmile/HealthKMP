package com.viktormykhailiv.kmp.health.dataType

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.LocalHealthManager
import com.viktormykhailiv.kmp.health.aggregate.BodyTemperatureAggregatedRecord
import com.viktormykhailiv.kmp.health.dataType.base.DataTypeTextFieldScreen
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.BodyTemperatureRecord
import com.viktormykhailiv.kmp.health.region.TemperatureRegionalPreference
import com.viktormykhailiv.kmp.health.region.preferred
import com.viktormykhailiv.kmp.health.units.Temperature
import kotlin.random.Random
import kotlin.time.Clock

@Composable
fun BodyTemperatureScreen() {
    DataTypeTextFieldScreen(
        title = "Body temperature",
        type = HealthDataType.BodyTemperature,
        initialValue = { Random.nextInt(356, 399) / 10.0 },
        serializer = { it.toString() },
        deserializer = { it.toDoubleOrNull() ?: 0.0 },
        writer = { bodyTemperature ->
            listOf(
                BodyTemperatureRecord(
                    time = Clock.System.now(),
                    temperature = Temperature.celsius(bodyTemperature),
                    measurementLocation = null,
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        aggregatedContent = { record: BodyTemperatureAggregatedRecord ->
            Text("Average ${record.avg}")
            Text("Min ${record.min}")
            Text("Max ${record.max}")
        },
        listContent = { temperature ->
            val health = LocalHealthManager.current
            var unit by remember { mutableStateOf(TemperatureRegionalPreference.Celsius) }
            LaunchedEffect(Unit) {
                unit = health.getRegionalPreferences()
                    .map { it.temperature }
                    .getOrElse { TemperatureRegionalPreference.Celsius }
            }

            val average = temperature.map { it.temperature.inCelsius }
                .average()
                .let { Temperature.celsius(it).preferred(unit) }
            val min = temperature.minOfOrNull { it.temperature.inCelsius }
                ?.let { Temperature.celsius(it).preferred(unit) }
            val max = temperature.maxOfOrNull { it.temperature.inCelsius }
                ?.let { Temperature.celsius(it).preferred(unit) }
            Text("Average $average")
            Text("Min $min")
            Text("Max $max")
        },
    )
}
