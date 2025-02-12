package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
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