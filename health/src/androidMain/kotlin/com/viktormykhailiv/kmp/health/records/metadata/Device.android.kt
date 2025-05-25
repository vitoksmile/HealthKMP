package com.viktormykhailiv.kmp.health.records.metadata

import android.os.Build
import com.viktormykhailiv.kmp.health.ApplicationContextHolder

actual fun Device.Companion.getLocalDevice(): Device = Device(
    type = getDeviceType(),
    manufacturer = Build.MANUFACTURER,
    model = Build.MODEL,
)

private fun getDeviceType(): DeviceType {
    val packageManager = ApplicationContextHolder.applicationContext.packageManager
    return when {
        packageManager.hasSystemFeature("android.hardware.type.watch") -> DeviceType.Watch
        else -> DeviceType.Phone
    }
}
