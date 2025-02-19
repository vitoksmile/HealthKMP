package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.units.Mass
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDate
import platform.HealthKit.HKCategorySample
import platform.HealthKit.HKCategoryTypeIdentifierSleepAnalysis
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepCore
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepDeep
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepREM
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepUnspecified
import platform.HealthKit.HKCategoryValueSleepAnalysisAwake
import platform.HealthKit.HKObject
import platform.HealthKit.HKObjectType
import platform.HealthKit.HKQuantity
import platform.HealthKit.HKQuantitySample
import platform.HealthKit.HKQuantityType
import platform.HealthKit.HKQuantityTypeIdentifier
import platform.HealthKit.HKQuantityTypeIdentifierBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierStepCount
import platform.HealthKit.HKUnit
import platform.HealthKit.countUnit
import platform.HealthKit.poundUnit
import kotlin.math.roundToInt

internal fun HealthRecord.toHKObjects(): List<HKObject>? {
    val record = this

    val quantityTypeIdentifier: HKQuantityTypeIdentifier
    val quantity: HKQuantity
    val startDate: NSDate
    val endDate: NSDate

    when (record) {
        is SleepSessionRecord -> {
            val typeIdentifier = HKObjectType
                .categoryTypeForIdentifier(HKCategoryTypeIdentifierSleepAnalysis)!!

            return record.stages.map { stage ->
                val sleepCategory = when (stage.type) {
                    SleepStageType.Unknown -> HKCategoryValueSleepAnalysisAsleepUnspecified
                    SleepStageType.Awake -> HKCategoryValueSleepAnalysisAwake
                    SleepStageType.AwakeInBed -> HKCategoryValueSleepAnalysisAwake
                    SleepStageType.Sleeping -> HKCategoryValueSleepAnalysisAsleepCore
                    SleepStageType.OutOfBed -> HKCategoryValueSleepAnalysisAwake
                    SleepStageType.Light -> HKCategoryValueSleepAnalysisAsleepCore
                    SleepStageType.Deep -> HKCategoryValueSleepAnalysisAsleepDeep
                    SleepStageType.REM -> HKCategoryValueSleepAnalysisAsleepREM
                }
                HKCategorySample.categorySampleWithType(
                    type = typeIdentifier,
                    value = sleepCategory,
                    startDate = stage.startTime.toNSDate(),
                    endDate = stage.endTime.toNSDate(),
                )
            }
        }

        is StepsRecord -> {
            quantityTypeIdentifier = HKQuantityTypeIdentifierStepCount
            quantity = HKQuantity.quantityWithUnit(
                unit = HKUnit.countUnit(),
                doubleValue = record.count.toDouble(),
            )
            startDate = record.startTime.toNSDate()
            endDate = record.endTime.toNSDate()
        }

        is WeightRecord -> {
            quantityTypeIdentifier = HKQuantityTypeIdentifierBodyMass
            quantity = HKQuantity.quantityWithUnit(
                unit = HKUnit.poundUnit(),
                doubleValue = record.weight.inPounds,
            )
            startDate = record.time.toNSDate()
            endDate = record.time.toNSDate()
        }

        else -> return null
    }

    return listOf(
        HKQuantitySample.Companion.quantitySampleWithType(
            quantityType = HKQuantityType.quantityTypeForIdentifier(quantityTypeIdentifier)
                ?: return null,
            quantity = quantity,
            startDate = startDate,
            endDate = endDate,
        )
    )
}

internal fun List<HKCategorySample>.toHealthRecords(): List<HealthRecord> {
    if (isEmpty()) return emptyList()

    return when (first().categoryType.identifier) {
        HKCategoryTypeIdentifierSleepAnalysis -> {
            map { sample ->
                val startTime = sample.startDate.toKotlinInstant()
                val endTime = sample.endDate.toKotlinInstant()
                val type = when (sample.value) {
                    HKCategoryValueSleepAnalysisAwake -> SleepStageType.Awake
                    HKCategoryValueSleepAnalysisAsleepCore -> SleepStageType.Light
                    HKCategoryValueSleepAnalysisAsleepDeep -> SleepStageType.Deep
                    HKCategoryValueSleepAnalysisAsleepREM -> SleepStageType.REM
                    else -> SleepStageType.Unknown
                }
                SleepSessionRecord.Stage(
                    startTime = startTime,
                    endTime = endTime,
                    type = type,
                )
            }.groupByRecords()
        }

        else -> emptyList()
    }
}

internal fun HKQuantitySample.toHealthRecord(): HealthRecord? {
    val sample = this

    return when (sample.quantityType.identifier) {
        HKQuantityTypeIdentifierStepCount -> {
            StepsRecord(
                startTime = sample.startDate.toKotlinInstant(),
                endTime = sample.endDate.toKotlinInstant(),
                count = sample.quantity.doubleValueForUnit(HKUnit.countUnit()).roundToInt(),
            )
        }

        HKQuantityTypeIdentifierBodyMass -> {
            WeightRecord(
                time = sample.startDate.toKotlinInstant(),
                weight = Mass.pounds(sample.quantity.doubleValueForUnit(HKUnit.poundUnit())),
            )
        }

        else -> null
    }
}