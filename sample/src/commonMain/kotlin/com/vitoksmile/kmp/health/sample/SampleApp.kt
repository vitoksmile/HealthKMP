package com.vitoksmile.kmp.health.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vitoksmile.kmp.health.HealthDataType
import com.vitoksmile.kmp.health.HealthManagerFactory
import kotlinx.coroutines.launch

@Composable
fun SampleApp() {
    val coroutineScope = rememberCoroutineScope()
    val healthManager = remember { HealthManagerFactory().createManager() }

    val readTypes = remember { listOf(HealthDataType.STEPS) }
    val writeTypes = remember { listOf(HealthDataType.WEIGHT) }

    var isAvailableResult by remember { mutableStateOf(Result.success(false)) }
    var isAuthorizedResult by remember { mutableStateOf<Result<Boolean>?>(null) }
    var isRevokeSupported by remember { mutableStateOf(false) }

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
        }
    }
}

expect fun getPlatformName(): String