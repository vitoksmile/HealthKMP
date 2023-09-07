package com.vitoksmile.kmp.health.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.vitoksmile.kmp.health.HealthDataType
import com.vitoksmile.kmp.health.HealthManagerFactory
import com.vitoksmile.kmp.health.HealthRecord
import com.vitoksmile.kmp.health.records.StepsRecord
import com.vitoksmile.kmp.health.records.WeightRecord
import com.vitoksmile.kmp.health.units.Mass
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@Composable
fun SampleApp() {
    val coroutineScope = rememberCoroutineScope()
    val healthManager = remember { HealthManagerFactory().createManager() }

    val readTypes = remember {
        listOf(
            HealthDataType.STEPS,
            HealthDataType.WEIGHT,
        )
    }
    val writeTypes = remember {
        listOf(
            HealthDataType.STEPS,
            HealthDataType.WEIGHT,
        )
    }

    var isAvailableResult by remember { mutableStateOf(Result.success(false)) }
    var isAuthorizedResult by remember { mutableStateOf<Result<Boolean>?>(null) }
    var isRevokeSupported by remember { mutableStateOf(false) }

    val data = remember { mutableStateMapOf<HealthDataType, Result<List<HealthRecord>>>() }

    LaunchedEffect(healthManager) {
        isAvailableResult = healthManager.isAvailable()

        if (isAvailableResult.getOrNull() == false) return@LaunchedEffect
        isAuthorizedResult = healthManager.isAuthorized(
            readTypes = readTypes,
            writeTypes = writeTypes,
        )
        isRevokeSupported = healthManager.isRevokeAuthorizationSupported().getOrNull() ?: false
    }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize()
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
                    Text("HealthManager isAvailable=${it.message}")
                }

            isAuthorizedResult
                ?.onSuccess {
                    Text("HealthManager isAuthorized=$it")
                }
                ?.onFailure {
                    Text("HealthManager isAuthorized=${it.message}")
                }
            if (isAvailableResult.getOrNull() == true && isAuthorizedResult?.getOrNull() == false)
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isAuthorizedResult = healthManager.requestAuthorization(
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
                            healthManager.revokeAuthorization()
                            isAuthorizedResult = healthManager.isAuthorized(
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
                                    data[type] = healthManager.readData(
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
                                Column {
                                    Text("count ${records.size}")

                                    records.forEach { record ->
                                        Text("Record $record")
                                    }
                                }
                            }
                            ?.onFailure {
                                Text("Failed to read records $it")
                            }

                        Divider()
                    }

                    Spacer(modifier = Modifier.height(64.dp))
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
                                writeSteps = healthManager.writeData(
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

                    Spacer(modifier = Modifier.height(16.dp))
                    var weight by remember { mutableStateOf(61) }
                    TextField(
                        value = weight.toString(),
                        onValueChange = { weight = it.toIntOrNull() ?: 0 },
                        label = { Text("Weight") },
                        keyboardOptions = remember { KeyboardOptions(keyboardType = KeyboardType.Number) },
                    )
                    var writeWeight by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                writeWeight = healthManager.writeData(
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
                        Text("Write $weight weight")
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