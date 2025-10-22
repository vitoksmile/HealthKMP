package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.HealthDataType.BloodGlucose
import com.viktormykhailiv.kmp.health.HealthDataType.BloodPressure
import com.viktormykhailiv.kmp.health.HealthDataType.BodyFat
import com.viktormykhailiv.kmp.health.HealthDataType.BodyTemperature
import com.viktormykhailiv.kmp.health.HealthDataType.Exercise
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Height
import com.viktormykhailiv.kmp.health.HealthDataType.LeanBodyMass
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import platform.HealthKit.HKCategoryType
import platform.HealthKit.HKCategoryTypeIdentifierSleepAnalysis
import platform.HealthKit.HKObjectType
import platform.HealthKit.HKQuantityType
import platform.HealthKit.HKQuantityTypeIdentifierActiveEnergyBurned
import platform.HealthKit.HKQuantityTypeIdentifierBloodGlucose
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureDiastolic
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureSystolic
import platform.HealthKit.HKQuantityTypeIdentifierBodyFatPercentage
import platform.HealthKit.HKQuantityTypeIdentifierBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierBodyTemperature
import platform.HealthKit.HKQuantityTypeIdentifierCyclingPower
import platform.HealthKit.HKQuantityTypeIdentifierCyclingSpeed
import platform.HealthKit.HKQuantityTypeIdentifierDistanceWalkingRunning
import platform.HealthKit.HKQuantityTypeIdentifierFlightsClimbed
import platform.HealthKit.HKQuantityTypeIdentifierHeartRate
import platform.HealthKit.HKQuantityTypeIdentifierHeight
import platform.HealthKit.HKQuantityTypeIdentifierLeanBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierRunningSpeed
import platform.HealthKit.HKQuantityTypeIdentifierStepCount
import platform.HealthKit.HKSampleType
import platform.HealthKit.HKSeriesType

internal fun HealthDataType.toHKSampleType(): List<HKSampleType?> = when (this) {
    BloodGlucose ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodGlucose))

    BloodPressure ->
        listOf(
            HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureSystolic),
            HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureDiastolic),
        )

    BodyFat ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyFatPercentage))

    BodyTemperature ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyTemperature))

    is Exercise -> {
        buildList {
            add(HKSampleType.workoutType())
            add(HKSeriesType.workoutRouteType())

            fun addIf(condition: Boolean, typeIdentifier: String?) {
                if (condition) {
                    add(HKObjectType.quantityTypeForIdentifier(typeIdentifier))
                }
            }

            addIf(activeEnergyBurned, HKQuantityTypeIdentifierActiveEnergyBurned)
            addIf(cyclingPower, HKQuantityTypeIdentifierCyclingPower)
            addIf(cyclingSpeed, HKQuantityTypeIdentifierCyclingSpeed)
            addIf(flightsClimbed, HKQuantityTypeIdentifierFlightsClimbed)
            addIf(distanceWalkingRunning, HKQuantityTypeIdentifierDistanceWalkingRunning)
            addIf(runningSpeed, HKQuantityTypeIdentifierRunningSpeed)
        }
    }

    HeartRate ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRate))

    Height ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeight))

    LeanBodyMass ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierLeanBodyMass))

    Sleep ->
        listOf(HKCategoryType.categoryTypeForIdentifier(HKCategoryTypeIdentifierSleepAnalysis))

    Steps ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierStepCount))

    Weight ->
        listOf(HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyMass))
}