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
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.sleep.SleepSessionCanvas
import com.viktormykhailiv.kmp.health.units.Mass
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun SampleApp() {
    val coroutineScope = rememberCoroutineScope()
    val health = remember { HealthManagerFactory().createManager() }

    val readTypes = remember {
        listOf(
            Sleep,
            Steps,
            Weight,
        )
    }
    val writeTypes = remember {
        listOf(
            Sleep,
            Steps,
            Weight,
        )
    }

    var isAvailableResult by remember { mutableStateOf(Result.success(false)) }
    var isAuthorizedResult by remember { mutableStateOf<Result<Boolean>?>(null) }
    var isRevokeSupported by remember { mutableStateOf(false) }

    val data = remember { mutableStateMapOf<HealthDataType, Result<List<HealthRecord>>>() }

    LaunchedEffect(health) {
        isAvailableResult = health.isAvailable()

        if (isAvailableResult.getOrNull() == false) return@LaunchedEffect
        isAuthorizedResult = health.isAuthorized(
            readTypes = readTypes,
            writeTypes = writeTypes,
        )
        isRevokeSupported = health.isRevokeAuthorizationSupported().getOrNull() ?: false
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
                .onSuccess { isAvailable ->
                    Text("HealthManager isAvailable=$isAvailable")
                }
                .onFailure {
                    Text("HealthManager isAvailable=$it")
                }

            isAuthorizedResult
                ?.onSuccess {
                    Text("HealthManager isAuthorized=$it")
                }
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
                    Text("Request authorization")
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
                                    data[type] = health.readData(
                                        startTime = Clock.System.now()
                                            .minus(1.days),
                                        endTime = Clock.System.now(),
                                        type = type,
                                    )
                                }
                            },
                        ) {
                            Text("Read $type")
                        }

                        data[type]
                            ?.onSuccess { records ->
                                Text("$type records count ${records.size}")

                                Spacer(Modifier.size(16.dp))
                                when (type) {
                                    Sleep -> {
                                        records
                                            .filterIsInstance<SleepSessionRecord>()
                                            .forEach { record ->
                                                Text("Sleep duration ${record.duration}")
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
                                        Text("Steps average $average")
                                        Text("Steps total $total")
                                    }

                                    Weight -> {
                                        val weight = records.filterIsInstance<WeightRecord>()
                                        val average = weight.map { it.weight.inKilograms }.average()
                                        val min = weight.minOf { it.weight.inKilograms }
                                        val max = weight.maxOf { it.weight.inKilograms }
                                        Text("Weight average $average kg")
                                        Text("Weight min $min kg")
                                        Text("Weight max $max kg")
                                    }
                                }
                            }
                            ?.onFailure {
                                Text("Failed to read $type records $it")
                            }

                        Divider()
                    }

                    Spacer(modifier = Modifier.height(64.dp))
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