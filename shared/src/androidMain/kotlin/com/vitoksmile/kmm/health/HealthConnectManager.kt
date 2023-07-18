package com.vitoksmile.kmm.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient

class HealthConnectManager(
    private val context: Context,
) : HealthManager {

    override fun isAvailable(): Result<Boolean> = runCatching {
        val status = HealthConnectClient.getSdkStatus(context)
        status == HealthConnectClient.SDK_AVAILABLE
    }
}