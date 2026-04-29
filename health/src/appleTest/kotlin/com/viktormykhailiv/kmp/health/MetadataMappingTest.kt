@file:OptIn(UnsafeNumber::class)
@file:Suppress("UNCHECKED_CAST")

package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.records.metadata.Device
import com.viktormykhailiv.kmp.health.records.metadata.DeviceType
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import kotlinx.cinterop.UnsafeNumber
import platform.Foundation.NSUUID
import platform.HealthKit.HKDevice
import platform.HealthKit.HKMetadataKeyWasUserEntered
import kotlin.test.Test
import kotlin.test.assertEquals

class MetadataMappingTest {

    @Test
    fun metadataMapping() {
        val uuid = NSUUID()
        val hkDevice = HKDevice(
            name = "Apple Watch",
            manufacturer = "Apple",
            model = "Watch",
            hardwareVersion = "7.0",
            firmwareVersion = "1.0",
            softwareVersion = "10.0",
            localIdentifier = "123",
            UDIDeviceIdentifier = "456"
        )
        val metadataMap: Map<Any?, Any?> = mapOf(HKMetadataKeyWasUserEntered to true)

        val result = metadataMap.toHealthMetadata(id = uuid, device = hkDevice)

        assertEquals(uuid.UUIDString, result.id)
        assertEquals("Apple", result.device?.manufacturer)
        assertEquals("Apple Watch", result.device?.model)
        assertEquals(DeviceType.Watch, result.device?.type)
        assertEquals(Metadata.RecordingMethod.ManualEntry, result.recordingMethod)

        val autoResult = (emptyMap<Any?, Any?>() as Map<Any?, Any?>?).toHealthMetadata(id = uuid, device = hkDevice)
        assertEquals(Metadata.RecordingMethod.AutoRecorded, autoResult.recordingMethod)

        val unknownResult = (emptyMap<Any?, Any?>() as Map<Any?, Any?>?).toHealthMetadata(id = uuid, device = null)
        assertEquals(Metadata.RecordingMethod.Unknown, unknownResult.recordingMethod)
    }
}
