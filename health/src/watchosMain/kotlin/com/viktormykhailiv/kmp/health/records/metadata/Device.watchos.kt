package com.viktormykhailiv.kmp.health.records.metadata

import platform.WatchKit.WKInterfaceDevice

actual fun Device.Companion.getLocalDevice(): Device = Device(
    type = DeviceType.Watch,
    manufacturer = "Apple",
    model = WKInterfaceDevice.currentDevice().localizedModel,
)
