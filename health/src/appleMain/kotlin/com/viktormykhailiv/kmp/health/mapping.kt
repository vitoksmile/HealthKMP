@file:OptIn(UnsafeNumber::class)

package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.records.BloodGlucoseRecord
import com.viktormykhailiv.kmp.health.records.BloodPressureRecord
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.HeightRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.records.metadata.Device
import com.viktormykhailiv.kmp.health.records.metadata.DeviceType
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.units.Length
import com.viktormykhailiv.kmp.health.units.BloodGlucose as BloodGlucoseUnit
import com.viktormykhailiv.kmp.health.units.Mass
import com.viktormykhailiv.kmp.health.units.Pressure
import kotlinx.cinterop.UnsafeNumber
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
import platform.HealthKit.HKQuantityTypeIdentifierBloodGlucose
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureDiastolic
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureSystolic
import platform.HealthKit.HKQuantityTypeIdentifierBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierHeartRate
import platform.HealthKit.HKQuantityTypeIdentifierHeight
import platform.HealthKit.HKQuantityTypeIdentifierStepCount
import platform.HealthKit.HKUnit
import platform.HealthKit.HKUnitMolarMassBloodGlucose
import platform.HealthKit.countUnit
import platform.HealthKit.literUnit
import platform.HealthKit.meterUnit
import platform.HealthKit.millimeterOfMercuryUnit
import platform.HealthKit.minuteUnit
import platform.HealthKit.moleUnitWithMolarMass
import platform.HealthKit.poundUnit
import platform.HealthKit.unitDividedByUnit
import kotlin.collections.orEmpty

// region Write
internal fun HealthRecord.toHKObjects(): List<HKObject>? {
    val record = this

    val quantityTypeIdentifier: HKQuantityTypeIdentifier
    val quantity: HKQuantity
    val startDate: NSDate
    val endDate: NSDate
    val metadata = record.metadata.toHKMetadata()

    when (record) {
        is BloodGlucoseRecord -> {
            quantityTypeIdentifier = HKQuantityTypeIdentifierBloodGlucose
            quantity = HKQuantity.quantityWithUnit(
                unit = bloodGlucoseUnit,
                // bloodGlucoseUnit is in moles per liter
                doubleValue = record.level.inMillimolesPerLiter / 1_000,
            )
            startDate = record.time.toNSDate()
            endDate = record.time.toNSDate()

            when (record.relationToMeal) {
                BloodGlucoseRecord.RelationToMeal.BeforeMeal -> {
                    metadata[HKMetadataKeyBloodGlucoseMealTime] = HKBloodGlucoseMealTimePreprandial
                }

                BloodGlucoseRecord.RelationToMeal.AfterMeal -> {
                    metadata[HKMetadataKeyBloodGlucoseMealTime] = HKBloodGlucoseMealTimePostprandial
                }

                BloodGlucoseRecord.RelationToMeal.General,
                BloodGlucoseRecord.RelationToMeal.Fasting,
                null -> {
                }
            }
        }

        is BloodPressureRecord -> {
            return listOf(
                HKQuantitySample.quantitySampleWithType(
                    quantityType = HKQuantityType.quantityTypeForIdentifier(
                        HKQuantityTypeIdentifierBloodPressureSystolic
                    ) ?: return null,
                    quantity = HKQuantity.quantityWithUnit(
                        unit = bloodPressureUnit,
                        doubleValue = record.systolic.inMillimetersOfMercury,
                    ),
                    startDate = record.time.toNSDate(),
                    endDate = record.time.toNSDate(),
                    metadata = metadata,
                ),
                HKQuantitySample.quantitySampleWithType(
                    quantityType = HKQuantityType.quantityTypeForIdentifier(
                        HKQuantityTypeIdentifierBloodPressureDiastolic
                    ) ?: return null,
                    quantity = HKQuantity.quantityWithUnit(
                        unit = bloodPressureUnit,
                        doubleValue = record.diastolic.inMillimetersOfMercury,
                    ),
                    startDate = record.time.toNSDate(),
                    endDate = record.time.toNSDate(),
                    metadata = metadata,
                ),
            )
        }

        is HeartRateRecord -> {
            quantityTypeIdentifier = HKQuantityTypeIdentifierHeartRate

            return record.samples.map { sample ->
                HKQuantitySample.quantitySampleWithType(
                    quantityType = HKQuantityType.quantityTypeForIdentifier(quantityTypeIdentifier)
                        ?: return null,
                    quantity = HKQuantity.quantityWithUnit(
                        unit = heartRateUnit,
                        doubleValue = sample.beatsPerMinute.toDouble(),
                    ),
                    startDate = sample.time.toNSDate(),
                    endDate = sample.time.toNSDate(),
                    metadata = metadata,
                )
            }
        }

        is HeightRecord -> {
            quantityTypeIdentifier = HKQuantityTypeIdentifierHeight
            quantity = HKQuantity.quantityWithUnit(
                unit = heightUnit,
                doubleValue = record.height.inMeters,
            )
            startDate = record.time.toNSDate()
            endDate = record.time.toNSDate()
        }

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
                    metadata = metadata,
                )
            }
        }

        is StepsRecord -> {
            quantityTypeIdentifier = HKQuantityTypeIdentifierStepCount
            quantity = HKQuantity.quantityWithUnit(
                unit = stepsUnit,
                doubleValue = record.count.toDouble(),
            )
            startDate = record.startTime.toNSDate()
            endDate = record.endTime.toNSDate()
        }

        is WeightRecord -> {
            quantityTypeIdentifier = HKQuantityTypeIdentifierBodyMass
            quantity = HKQuantity.quantityWithUnit(
                unit = weightUnit,
                doubleValue = record.weight.inPounds,
            )
            startDate = record.time.toNSDate()
            endDate = record.time.toNSDate()
        }

        else -> return null
    }

    return listOf(
        HKQuantitySample.quantitySampleWithType(
            quantityType = HKQuantityType.quantityTypeForIdentifier(quantityTypeIdentifier)
                ?: return null,
            quantity = quantity,
            startDate = startDate,
            endDate = endDate,
            metadata = metadata,
        )
    )
}
// endregion

// region Read
internal fun List<HKCategorySample>.toHealthRecords(): List<HealthRecord> {
    if (isEmpty()) return emptyList()

    return when (first().categoryType.identifier) {
        HKCategoryTypeIdentifierSleepAnalysis -> {
            val metadata = firstOrNull()?.metadata.toMetadata()
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
            }.groupByRecords(metadata)
        }

        else -> emptyList()
    }
}

internal fun List<HKQuantitySample>.toHealthRecord(): List<HealthRecord> {
    if (isEmpty()) return emptyList()

    return when (first().quantityType.identifier) {
        HKQuantityTypeIdentifierBloodGlucose -> {
            map { sample ->
                BloodGlucoseRecord(
                    time = sample.startDate.toKotlinInstant(),
                    level = sample.quantity.bloodGlucoseValue,
                    specimenSource = null,
                    mealType = null,
                    relationToMeal = when (sample.metadata.orEmpty()[HKMetadataKeyBloodGlucoseMealTime]) {
                        HKBloodGlucoseMealTimePreprandial -> BloodGlucoseRecord.RelationToMeal.BeforeMeal
                        HKBloodGlucoseMealTimePostprandial -> BloodGlucoseRecord.RelationToMeal.AfterMeal
                        else -> null
                    },
                    metadata = sample.toMetadata(),
                )
            }
        }

        HKQuantityTypeIdentifierBloodPressureSystolic,
        HKQuantityTypeIdentifierBloodPressureDiastolic -> {
            val systolicList =
                this.filter { it.quantityType.identifier == HKQuantityTypeIdentifierBloodPressureSystolic }
                    .groupBy { it.startDate.toKotlinInstant() to it.endDate.toKotlinInstant() }
            val diastolicList =
                this.filter { it.quantityType.identifier == HKQuantityTypeIdentifierBloodPressureDiastolic }
                    .groupBy { it.startDate.toKotlinInstant() to it.endDate.toKotlinInstant() }

            val records = mutableListOf<BloodPressureRecord>()
            for ((date, systolicList) in systolicList) {
                val diastolicList = diastolicList[date] ?: continue

                for (i in systolicList.indices) {
                    if (i > systolicList.size - 1) break
                    val systolic = systolicList[i]
                    val diastolic = diastolicList[i]

                    records.add(
                        BloodPressureRecord(
                            time = date.first,
                            systolic = systolic.quantity.bloodPressureValue,
                            diastolic = diastolic.quantity.bloodPressureValue,
                            bodyPosition = null,
                            measurementLocation = null,
                            metadata = systolic.toMetadata(),
                        )
                    )
                }
            }
            records
        }

        HKQuantityTypeIdentifierHeartRate -> {
            val metadata = firstOrNull()?.metadata.toMetadata()
            map { sample ->
                HeartRateSampleInternal(
                    startTime = sample.startDate.toKotlinInstant(),
                    endTime = sample.endDate.toKotlinInstant(),
                    beatsPerMinute = sample.quantity.heartRateValue.toInt(),
                )
            }.group(metadata)
        }

        HKQuantityTypeIdentifierStepCount -> {
            map { sample ->
                StepsRecord(
                    startTime = sample.startDate.toKotlinInstant(),
                    endTime = sample.endDate.toKotlinInstant(),
                    count = sample.quantity.stepsValue.toInt(),
                    metadata = sample.toMetadata(),
                )
            }
        }

        HKQuantityTypeIdentifierBodyMass -> {
            map { sample ->
                WeightRecord(
                    time = sample.startDate.toKotlinInstant(),
                    weight = sample.quantity.weightValue,
                    metadata = sample.toMetadata(),
                )
            }
        }

        else -> emptyList()
    }
}
// endregion

// region Units
internal val HKQuantity?.bloodGlucoseValue: BloodGlucoseUnit
    get() = BloodGlucoseUnit.millimolesPerLiter(
        this?.doubleValueForUnit(bloodGlucoseUnit)?.times(1_000) ?: 0.0
    )

private val bloodGlucoseUnit: HKUnit
    get() = HKUnit.moleUnitWithMolarMass(HKUnitMolarMassBloodGlucose)
        .unitDividedByUnit(HKUnit.literUnit())

internal val HKQuantity?.bloodPressureValue: Pressure
    get() = Pressure.millimetersOfMercury(
        this?.doubleValueForUnit(bloodPressureUnit) ?: 0.0
    )

private val bloodPressureUnit: HKUnit
    get() = HKUnit.millimeterOfMercuryUnit()

internal val HKQuantity?.heartRateValue: Long
    get() = this?.doubleValueForUnit(heartRateUnit)?.toLong() ?: 0L

private val heartRateUnit: HKUnit
    get() = HKUnit.countUnit().unitDividedByUnit(HKUnit.minuteUnit())

internal val HKQuantity?.heightValue: Length
    get() = Length.meters(
        this?.doubleValueForUnit(heightUnit) ?: 0.0
    )

private val heightUnit: HKUnit
    get() = HKUnit.meterUnit()

internal val HKQuantity?.stepsValue: Long
    get() = this?.doubleValueForUnit(stepsUnit)?.toLong() ?: 0L

private val stepsUnit: HKUnit
    get() = HKUnit.countUnit()

internal val HKQuantity?.weightValue: Mass
    get() = Mass.pounds(
        this?.doubleValueForUnit(weightUnit) ?: 0.0
    )

private val weightUnit: HKUnit
    get() = HKUnit.poundUnit()
// endregion

// region Metadata
private fun HKQuantitySample.toMetadata(): Metadata {
    return metadata.toMetadata()
}

private fun Map<Any?, *>?.toMetadata(): Metadata {
    val metadata = this.orEmpty()
    val id = metadata[HKMetadataKeyExternalUUID] as? String ?: Metadata.EMPTY_ID

    val deviceManufacturer = metadata[HKMetadataKeyDeviceManufacturerName] as? String
    val deviceName = metadata[HKMetadataKeyDeviceName] as? String
    val device = if (deviceManufacturer != null || deviceName != null) {
        Device(
            type = DeviceType.Unknown,
            manufacturer = deviceManufacturer,
            model = deviceName,
        )
    } else {
        null
    }

    return if (metadata[HKMetadataKeyWasUserEntered] == true || device == null) {
        Metadata.manualEntry(id = id)
    } else {
        Metadata.autoRecorded(id = id, device = device)
    }
}

private fun Metadata.toHKMetadata(): MutableMap<Any?, Any> {
    return buildMap {
        put(HKMetadataKeyWasUserEntered, recordingMethod is Metadata.RecordingMethod.ManualEntry)
        id.takeIf { it != Metadata.EMPTY_ID }?.let { put(HKMetadataKeyExternalUUID, id) }
        device?.manufacturer?.let { put(HKMetadataKeyDeviceManufacturerName, it) }
        device?.model?.let { put(HKMetadataKeyDeviceName, it) }
    }.toMutableMap()
}

/**
 * https://developer.apple.com/documentation/healthkit/metadata-keys
 */
// General Keys
private const val HKMetadataKeyExternalUUID = "HKExternalUUID"
private const val HKMetadataKeyWasUserEntered = "HKWasUserEntered"

// Device Information Keys
private const val HKMetadataKeyDeviceManufacturerName = "HKDeviceManufacturerName"
private const val HKMetadataKeyDeviceName = "HKDeviceName"

// Blood glucose
private const val HKMetadataKeyBloodGlucoseMealTime = "HKBloodGlucoseMealTime"
private const val HKBloodGlucoseMealTimePreprandial = 1.0
private const val HKBloodGlucoseMealTimePostprandial = 2.0
// endregion