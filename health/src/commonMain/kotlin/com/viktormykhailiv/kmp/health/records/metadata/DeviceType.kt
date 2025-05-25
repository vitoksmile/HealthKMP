package com.viktormykhailiv.kmp.health.records.metadata

sealed interface DeviceType {

    data object Unknown : DeviceType

    data object Watch : DeviceType

    data object Phone : DeviceType

    data object Scale : DeviceType

    data object Ring : DeviceType

    data object HeadMounted : DeviceType

    data object FitnessBand : DeviceType

    data object ChestStrap : DeviceType

    data object SmartDisplay : DeviceType

}
