package com.viktormykhailiv.kmp.health.sample.dataType

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.aggregate.ActiveEnergyBurnedAggregatedRecord
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.ActiveEnergyBurnedRecord
import com.viktormykhailiv.kmp.health.sample.dataType.base.DataTypeTextFieldScreen
import com.viktormykhailiv.kmp.health.units.Energy
import com.viktormykhailiv.kmp.health.units.kilocalories
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

@Composable
fun ActiveEnergyBurnedScreen() {
    DataTypeTextFieldScreen(
        title = "Active Energy Burned, kcal",
        type = HealthDataType.ActiveEnergyBurned,
        initialValue = { Random.nextInt(1, 1000).toDouble() },
        serializer = { it.toString() },
        deserializer = { it.toDoubleOrNull() ?: 0.0 },
        writer = { energy ->
            listOf(
                ActiveEnergyBurnedRecord(
                    startTime = Clock.System.now().minus(1.hours),
                    endTime = Clock.System.now(),
                    energy = Energy.kilocalories(energy),
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        aggregatedContent = { record: ActiveEnergyBurnedAggregatedRecord ->
            Text("Total ${record.energy}")
        },
        groupedAggregatedContent = { records ->
            Text("Grouped Aggregate divided the aggregate data into ${records.size} slices")
            Text("A slice has an average of: ${records.sumOf { it.energy.inKilocalories } / records.size} kilocalories")
            Text("The slices have a total of: ${records.sumOf { it.energy.inKilocalories }} kilocalories")
        },
        listContent = { energyRecords ->
            val average = energyRecords.map { it.energy.inKilocalories }.average().kilocalories
            val total = energyRecords.sumOf { it.energy.inKilocalories }.kilocalories
            Text("Average $average")
            Text("Total $total")
        },
    )
}
