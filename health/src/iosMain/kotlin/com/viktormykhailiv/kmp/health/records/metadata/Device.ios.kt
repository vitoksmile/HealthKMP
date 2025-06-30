package com.viktormykhailiv.kmp.health.records.metadata

import platform.UIKit.UIDevice

actual fun Device.Companion.getLocalDevice(): Device = Device(
    type = DeviceType.Phone,
    manufacturer = "Apple",
    model = UIDevice.currentDevice().localizedModel,
)
