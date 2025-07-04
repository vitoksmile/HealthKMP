package com.viktormykhailiv.kmp.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.aggregate.HeartRateAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.SleepAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.StepsAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.WeightAggregatedRecord
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.sleep.SleepSessionCanvas
import com.viktormykhailiv.kmp.health.units.Mass
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun SampleApp() {
    val coroutineScope = rememberCoroutineScope()
    val health = remember { HealthManagerFactory().createManager() }

    val readTypes = remember {
        listOf(
            HeartRate,
            Sleep,
            Steps,
            Weight,
        )
    }
    val writeTypes = remember {
        listOf(
            HeartRate,
            Sleep,
            Steps,
            Weight,
        )
    }

    var isAvailableResult by remember { mutableStateOf(Result.success(false)) }
    var isAuthorizedResult by remember { mutableStateOf<Result<Boolean>?>(null) }
    var isRevokeSupported by remember { mutableStateOf(false) }

    val records = remember {
        mutableStateMapOf<HealthDataType, Result<List<HealthRecord>>>()
    }
    val aggregatedRecords = remember {
        mutableStateMapOf<HealthDataType, Result<HealthAggregatedRecord>>()
    }

    LaunchedEffect(health) {
        isAvailableResult = health.isAvailable()

        if (isAvailableResult.getOrNull() == false) return@LaunchedEffect
        isAuthorizedResult = health.isAuthorized(
            readTypes = readTypes,
            writeTypes = writeTypes,
        )
        isRevokeSupported = health.isRevokeAuthorizationSupported().getOrNull() == true
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Hello, this is HealthKMP for ${getPlatformName()}")

            isAvailableResult
                .onFailure {
                    Text("HealthManager isAvailable=$it")
                }

            isAuthorizedResult
                ?.onFailure {
                    Text("HealthManager isAuthorized=$it")
                }
            if (isAvailableResult.getOrNull() == true && isAuthorizedResult?.getOrNull() != true)
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isAuthorizedResult = health.requestAuthorization(
                                readTypes = readTypes,
                                writeTypes = writeTypes,
                            )
                        }
                    },
                ) {
                    Text("Request permissions")
                }

            if (isAvailableResult.getOrNull() == true && isRevokeSupported && isAuthorizedResult?.getOrNull() == true)
                Button(
                    onClick = {
                        coroutineScope.launch {
                            health.revokeAuthorization()
                            isAuthorizedResult = health.isAuthorized(
                                readTypes = readTypes,
                                writeTypes = writeTypes,
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red,
                        contentColor = Color.White,
                    ),
                ) {
                    Text("Revoke authorization")
                }

            if (isAvailableResult.getOrNull() == true && isAuthorizedResult?.getOrNull() == true) {
                Column {
                    readTypes.forEach { type ->
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    records[type] = health.readData(
                                        startTime = Clock.System.now()
                                            .minus(7.days),
                                        endTime = Clock.System.now(),
                                        type = type,
                                    )
                                }
                            },
                        ) {
                            Text("Read $type")
                        }
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    aggregatedRecords[type] = health.aggregate(
                                        startTime = Clock.System.now()
                                            .minus(7.days),
                                        endTime = Clock.System.now(),
                                        type = type,
                                    )
                                }
                            },
                        ) {
                            Text("Aggregate $type")
                        }

                        records[type]
                            ?.onSuccess { records ->
                                Text("$type records count ${records.size}")

                                when (type) {
                                    is HeartRate -> {
                                        val heartRates = records.filterIsInstance<HeartRateRecord>()
                                            .map { it.samples }
                                            .flatten()
                                        val average = heartRates.map { it.beatsPerMinute }.average()
                                        val min = heartRates.minOfOrNull { it.beatsPerMinute }
                                        val max = heartRates.maxOfOrNull { it.beatsPerMinute }
                                        Text("Average $average")
                                        Text("Min $min")
                                        Text("Max $max")
                                    }

                                    Sleep -> {
                                        Spacer(Modifier.size(16.dp))
                                        records
                                            .filterIsInstance<SleepSessionRecord>()
                                            .forEach { record ->
                                                Text("Sleep session duration ${record.duration}")
                                                SleepSessionCanvas(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    record = record,
                                                )
                                                Spacer(Modifier.size(16.dp))
                                            }
                                    }

                                    Steps -> {
                                        val steps = records.filterIsInstance<StepsRecord>()
                                        val average = steps.map { it.count }.average()
                                        val total = steps.sumOf { it.count }
                                        Text("Average $average")
                                        Text("Total $total")
                                    }

                                    Weight -> {
                                        val weight = records.filterIsInstance<WeightRecord>()
                                        val average = weight.map { it.weight.inKilograms }.average()
                                        val min = weight.minOfOrNull { it.weight.inKilograms }
                                        val max = weight.maxOfOrNull { it.weight.inKilograms }
                                        Text("Average $average kg")
                                        Text("Min $min kg")
                                        Text("Max $max kg")
                                    }
                                }
                            }
                            ?.onFailure {
                                Text("Failed to read $type records $it")
                            }

                        aggregatedRecords[type]
                            ?.onSuccess { record ->
                                Spacer(Modifier.size(16.dp))
                                Text("Aggregated $type")

                                when (record) {
                                    is HeartRateAggregatedRecord -> {
                                        Text("Average ${record.avg}")
                                        Text("Min ${record.min}")
                                        Text("Max ${record.max}")
                                    }

                                    is SleepAggregatedRecord -> {
                                        Text("Total ${record.totalDuration}")
                                    }

                                    is StepsAggregatedRecord -> {
                                        Text("Total ${record.count}")
                                    }

                                    is WeightAggregatedRecord -> {
                                        Text("Average ${record.avg}")
                                        Text("Min ${record.min}")
                                        Text("Max ${record.max}")
                                    }
                                }
                            }
                            ?.onFailure {
                                Text("Failed to read $type records $it")
                            }

                        Divider()
                    }

                    Spacer(modifier = Modifier.height(64.dp))
                    var writeHeartRate by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val samplesCount = 6
                                val sampleInterval = 10.minutes
                                val endTime = Clock.System.now()
                                val startTime = endTime.minus(sampleInterval * samplesCount)
                                writeHeartRate = health.writeData(
                                    listOf(
                                        HeartRateRecord(
                                            startTime = startTime,
                                            endTime = endTime,
                                            samples = List(samplesCount) {
                                                HeartRateRecord.Sample(
                                                    time = startTime.plus((it * sampleInterval.inWholeMinutes).minutes),
                                                    beatsPerMinute = Random.nextInt(40, 300),
                                                )
                                            },
                                            metadata = generateManualEntryMetadata(),
                                        ),
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write heart rate")
                    }
                    writeHeartRate
                        ?.onSuccess {
                            Text("Heart rate wrote successfully")
                        }
                        ?.onFailure {
                            Text("Failed to write heart rate $it")
                        }

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    var writeSleep by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val startTime = Clock.System.now()
                                    .minus(12.hours)
                                val endTime = Clock.System.now()
                                    .minus(11.hours)
                                val types = listOf(
                                    SleepStageType.Awake,
                                    SleepStageType.OutOfBed,
                                    SleepStageType.Sleeping,
                                    SleepStageType.Light,
                                    SleepStageType.Deep,
                                    SleepStageType.REM,
                                )

                                writeSleep = health.writeData(
                                    listOf(
                                        SleepSessionRecord(
                                            startTime = startTime,
                                            endTime = endTime,
                                            stages = List(6) {
                                                SleepSessionRecord.Stage(
                                                    startTime = startTime.plus((10 * it).minutes),
                                                    endTime = startTime.plus((10 * it).minutes + 10.minutes),
                                                    type = types[it],
                                                )
                                            },
                                            metadata = generateManualEntryMetadata(),
                                        )
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write sleep")
                    }
                    writeSleep
                        ?.onSuccess {
                            Text("Sleep wrote successfully")
                        }
                        ?.onFailure {
                            Text("Failed to write sleep $it")
                        }

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    var steps by remember { mutableStateOf(100) }
                    TextField(
                        value = steps.toString(),
                        onValueChange = { steps = it.toIntOrNull() ?: 0 },
                        label = { Text("Steps") },
                        keyboardOptions = remember { KeyboardOptions(keyboardType = KeyboardType.Number) },
                    )
                    var writeSteps by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                writeSteps = health.writeData(
                                    listOf(
                                        StepsRecord(
                                            startTime = Clock.System.now()
                                                .minus(1.hours),
                                            endTime = Clock.System.now(),
                                            count = steps,
                                            metadata = generateManualEntryMetadata(),
                                        )
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write $steps steps")
                    }
                    writeSteps
                        ?.onSuccess {
                            Text("Steps wrote successfully")
                        }
                        ?.onFailure {
                            Text("Failed to write steps $it")
                        }

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    var weight by remember { mutableStateOf(61) }
                    TextField(
                        value = weight.toString(),
                        onValueChange = { weight = it.toIntOrNull() ?: 0 },
                        label = { Text("Weight, kg") },
                        keyboardOptions = remember { KeyboardOptions(keyboardType = KeyboardType.Number) },
                    )
                    var writeWeight by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                writeWeight = health.writeData(
                                    listOf(
                                        WeightRecord(
                                            time = Clock.System.now(),
                                            weight = Mass.kilograms(weight.toDouble()),
                                            metadata = generateManualEntryMetadata(),
                                        )
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write $weight kg")
                    }
                    writeWeight
                        ?.onSuccess {
                            Text("Weight wrote successfully")
                        }
                        ?.onFailure {
                            Text("Failed to write weight $it")
                        }
                }
            }
        }
    }
}

expect fun getPlatformName(): String
