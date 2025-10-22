package com.viktormykhailiv.kmp.health.records.metadata

import platform.HealthKit.HKDevice

actual fun Device.Companion.getLocalDevice(): Device {
    val device = HKDevice.localDevice()
    return Device(
        type = DeviceType.Phone,
        manufacturer = device.manufacturer,
        model = device.model,
    )
}
