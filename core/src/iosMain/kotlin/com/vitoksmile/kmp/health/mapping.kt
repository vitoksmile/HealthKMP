package com.vitoksmile.kmp.health

import com.vitoksmile.kmp.health.records.StepsRecord
import com.vitoksmile.kmp.health.records.WeightRecord
import com.vitoksmile.kmp.health.units.Mass
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDate
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

internal fun HealthRecord.toHKQuantitySample(): HKQuantitySample? {
    val record = this

    val quantityTypeIdentifier: HKQuantityTypeIdentifier
    val quantity: HKQuantity
    val startDate: NSDate
    val endDate: NSDate

    when (record) {
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

    return HKQuantitySample.Companion.quantitySampleWithType(
        quantityType = HKQuantityType.quantityTypeForIdentifier(quantityTypeIdentifier)
            ?: return null,
        quantity = quantity,
        startDate = startDate,
        endDate = endDate,
    )
}

internal fun HKQuantitySample.toHealthRecord(): HealthRecord? {
    val sample = this

    return when (sample.quantityType.identifier) {
        HKQuantityTypeIdentifierStepCount -> {
            StepsRecord(
                startTime = sample.startDate.toKotlinInstant(),
                endTime = sample.endDate.toKotlinInstant(),
                count = sample.quantity.doubleValueForUnit(HKUnit.countUnit())
                    .roundToInt(),
            )
        }

        HKQuantityTypeIdentifierBodyMass -> {
            WeightRecord(
                time = sample.startDate.toKotlinInstant(),
                weight = Mass.pounds(
                    sample.quantity.doubleValueForUnit(HKUnit.poundUnit()),
                ),
            )
        }

        else -> null
    }
}