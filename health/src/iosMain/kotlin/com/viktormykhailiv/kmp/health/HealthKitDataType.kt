package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import platform.HealthKit.HKCategoryType
import platform.HealthKit.HKCategoryTypeIdentifierSleepAnalysis
import platform.HealthKit.HKQuantityType
import platform.HealthKit.HKQuantityTypeIdentifierBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierHeartRate
import platform.HealthKit.HKQuantityTypeIdentifierStepCount
import platform.HealthKit.HKSampleType

internal fun HealthDataType.toHKSampleType(): HKSampleType? = when (this) {
    HeartRate ->
        HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRate)

    Sleep ->
        HKCategoryType.categoryTypeForIdentifier(HKCategoryTypeIdentifierSleepAnalysis)

    Steps ->
        HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierStepCount)

    Weight ->
        HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyMass)
}