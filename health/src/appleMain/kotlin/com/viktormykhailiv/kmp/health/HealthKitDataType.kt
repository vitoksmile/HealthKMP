package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.HealthDataType.BloodGlucose
import com.viktormykhailiv.kmp.health.HealthDataType.BloodPressure
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Height
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import platform.HealthKit.HKCategoryType
import platform.HealthKit.HKCategoryTypeIdentifierSleepAnalysis
import platform.HealthKit.HKQuantityType
import platform.HealthKit.HKQuantityTypeIdentifierBloodGlucose
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureDiastolic
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureSystolic
import platform.HealthKit.HKQuantityTypeIdentifierBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierHeartRate
import platform.HealthKit.HKQuantityTypeIdentifierHeight
import platform.HealthKit.HKQuantityTypeIdentifierStepCount
import platform.HealthKit.HKSampleType

internal fun HealthDataType.toHKSampleType(): List<HKSampleType?> = when (this) {
    BloodGlucose ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodGlucose))

    BloodPressure ->
        listOf(
            HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureSystolic),
            HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureDiastolic),
        )

    HeartRate ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRate))

    Height ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeight))

    Sleep ->
        listOf(HKCategoryType.categoryTypeForIdentifier(HKCategoryTypeIdentifierSleepAnalysis))

    Steps ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierStepCount))

    Weight ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyMass))
}