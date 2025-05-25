package com.viktormykhailiv.kmp.health.records.metadata

/**
 * A physical device (such as phone, watch, scale, or chest strap) which captured associated health
 * data point.
 *
 * Device needs to be populated by users of the API. Metadata fields not provided by clients will
 * remain absent.
 *
 * @property type a client supplied type of the device
 * @property manufacturer an optional client supplied manufacturer of the device
 * @property model an optional client supplied model of the device
 */
data class Device(
    val type: DeviceType,
    val manufacturer: String? = null,
    val model: String? = null,
) {

    companion object
}

expect fun Device.Companion.getLocalDevice(): Device
