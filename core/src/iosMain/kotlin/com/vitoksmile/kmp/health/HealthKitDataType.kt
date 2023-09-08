package com.vitoksmile.kmp.health

import com.vitoksmile.kmp.health.HealthDataType.Steps
import com.vitoksmile.kmp.health.HealthDataType.Weight
import platform.HealthKit.HKQuantityType
import platform.HealthKit.HKQuantityTypeIdentifierBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierStepCount
import platform.HealthKit.HKSampleType

internal fun HealthDataType.toHKSampleType(): HKSampleType? = when (this) {
    Steps ->
        HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierStepCount)

    Weight ->
        HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyMass)
}