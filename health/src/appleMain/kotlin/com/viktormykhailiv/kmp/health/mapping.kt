@file:OptIn(UnsafeNumber::class)

package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.records.BloodGlucoseRecord
import com.viktormykhailiv.kmp.health.records.BloodPressureRecord
import com.viktormykhailiv.kmp.health.records.BodyFatRecord
import com.viktormykhailiv.kmp.health.records.BodyTemperatureRecord
import com.viktormykhailiv.kmp.health.records.CyclingPedalingCadenceRecord
import com.viktormykhailiv.kmp.health.records.ExerciseLap
import com.viktormykhailiv.kmp.health.records.ExerciseRoute
import com.viktormykhailiv.kmp.health.records.ExerciseSegment
import com.viktormykhailiv.kmp.health.records.ExerciseSessionRecord
import com.viktormykhailiv.kmp.health.records.ExerciseType
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.HeightRecord
import com.viktormykhailiv.kmp.health.records.LeanBodyMassRecord
import com.viktormykhailiv.kmp.health.records.OvulationTestRecord
import com.viktormykhailiv.kmp.health.records.MenstruationFlowRecord
import com.viktormykhailiv.kmp.health.records.MenstruationPeriodRecord
import com.viktormykhailiv.kmp.health.records.PowerRecord
import com.viktormykhailiv.kmp.health.records.SexualActivityRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.records.metadata.Device
import com.viktormykhailiv.kmp.health.records.metadata.DeviceType
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.region.TemperatureRegionalPreference
import com.viktormykhailiv.kmp.health.units.Length
import com.viktormykhailiv.kmp.health.units.BloodGlucose as BloodGlucoseUnit
import com.viktormykhailiv.kmp.health.units.Mass
import com.viktormykhailiv.kmp.health.units.Percentage
import com.viktormykhailiv.kmp.health.units.Pressure
import com.viktormykhailiv.kmp.health.units.Temperature
import com.viktormykhailiv.kmp.health.units.percent
import com.viktormykhailiv.kmp.health.units.watts
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.useContents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toNSDate
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.Foundation.NSDate
import platform.Foundation.NSDateInterval
import platform.Foundation.compare
import platform.HealthKit.HKCategorySample
import platform.HealthKit.HKCategoryTypeIdentifierMenstrualFlow
import platform.HealthKit.HKCategoryTypeIdentifierOvulationTestResult
import platform.HealthKit.HKCategoryTypeIdentifierSexualActivity
import platform.HealthKit.HKCategoryTypeIdentifierSleepAnalysis
import platform.HealthKit.HKCategoryValueMenstrualFlowHeavy
import platform.HealthKit.HKCategoryValueMenstrualFlowLight
import platform.HealthKit.HKCategoryValueMenstrualFlowMedium
import platform.HealthKit.HKCategoryValueMenstrualFlowUnspecified
import platform.HealthKit.HKCategoryValueNotApplicable
import platform.HealthKit.HKCategoryValueOvulationTestResultEstrogenSurge
import platform.HealthKit.HKCategoryValueOvulationTestResultIndeterminate
import platform.HealthKit.HKCategoryValueOvulationTestResultLuteinizingHormoneSurge
import platform.HealthKit.HKCategoryValueOvulationTestResultNegative
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepCore
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepDeep
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepREM
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepUnspecified
import platform.HealthKit.HKCategoryValueSleepAnalysisAwake
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKMetadataKeyLapLength
import platform.HealthKit.HKMetadataKeyMenstrualCycleStart
import platform.HealthKit.HKObject
import platform.HealthKit.HKObjectType
import platform.HealthKit.HKQuantity
import platform.HealthKit.HKQuantitySample
import platform.HealthKit.HKQuantityType
import platform.HealthKit.HKQuantityTypeIdentifier
import platform.HealthKit.HKQuantityTypeIdentifierBloodGlucose
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureDiastolic
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureSystolic
import platform.HealthKit.HKQuantityTypeIdentifierBodyFatPercentage
import platform.HealthKit.HKQuantityTypeIdentifierBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierBodyTemperature
import platform.HealthKit.HKQuantityTypeIdentifierCyclingCadence
import platform.HealthKit.HKQuantityTypeIdentifierCyclingPower
import platform.HealthKit.HKQuantityTypeIdentifierHeartRate
import platform.HealthKit.HKQuantityTypeIdentifierHeight
import platform.HealthKit.HKQuantityTypeIdentifierLeanBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierStepCount
import platform.HealthKit.HKUnit
import platform.HealthKit.HKUnitMolarMassBloodGlucose
import platform.HealthKit.HKWorkout
import platform.HealthKit.HKWorkoutActivityType
import platform.HealthKit.HKWorkoutActivityTypeAmericanFootball
import platform.HealthKit.HKWorkoutActivityTypeAustralianFootball
import platform.HealthKit.HKWorkoutActivityTypeBadminton
import platform.HealthKit.HKWorkoutActivityTypeBaseball
import platform.HealthKit.HKWorkoutActivityTypeBasketball
import platform.HealthKit.HKWorkoutActivityTypeBoxing
import platform.HealthKit.HKWorkoutActivityTypeClimbing
import platform.HealthKit.HKWorkoutActivityTypeCricket
import platform.HealthKit.HKWorkoutActivityTypeDance
import platform.HealthKit.HKWorkoutActivityTypeDiscSports
import platform.HealthKit.HKWorkoutActivityTypeDownhillSkiing
import platform.HealthKit.HKWorkoutActivityTypeElliptical
import platform.HealthKit.HKWorkoutActivityTypeFencing
import platform.HealthKit.HKWorkoutActivityTypeGolf
import platform.HealthKit.HKWorkoutActivityTypeGymnastics
import platform.HealthKit.HKWorkoutActivityTypeHandball
import platform.HealthKit.HKWorkoutActivityTypeHighIntensityIntervalTraining
import platform.HealthKit.HKWorkoutActivityTypeHiking
import platform.HealthKit.HKWorkoutActivityTypeHockey
import platform.HealthKit.HKWorkoutActivityTypeMartialArts
import platform.HealthKit.HKWorkoutActivityTypeOther
import platform.HealthKit.HKWorkoutActivityTypePaddleSports
import platform.HealthKit.HKWorkoutActivityTypePilates
import platform.HealthKit.HKWorkoutActivityTypeRacquetball
import platform.HealthKit.HKWorkoutActivityTypeRowing
import platform.HealthKit.HKWorkoutActivityTypeRugby
import platform.HealthKit.HKWorkoutActivityTypeRunning
import platform.HealthKit.HKWorkoutActivityTypeSailing
import platform.HealthKit.HKWorkoutActivityTypeSkatingSports
import platform.HealthKit.HKWorkoutActivityTypeSnowSports
import platform.HealthKit.HKWorkoutActivityTypeSnowboarding
import platform.HealthKit.HKWorkoutActivityTypeSoccer
import platform.HealthKit.HKWorkoutActivityTypeSoftball
import platform.HealthKit.HKWorkoutActivityTypeSquash
import platform.HealthKit.HKWorkoutActivityTypeStairClimbing
import platform.HealthKit.HKWorkoutActivityTypeSurfingSports
import platform.HealthKit.HKWorkoutActivityTypeSwimBikeRun
import platform.HealthKit.HKWorkoutActivityTypeSwimming
import platform.HealthKit.HKWorkoutActivityTypeTableTennis
import platform.HealthKit.HKWorkoutActivityTypeTennis
import platform.HealthKit.HKWorkoutActivityTypeTraditionalStrengthTraining
import platform.HealthKit.HKWorkoutActivityTypeUnderwaterDiving
import platform.HealthKit.HKWorkoutActivityTypeVolleyball
import platform.HealthKit.HKWorkoutActivityTypeWalking
import platform.HealthKit.HKWorkoutActivityTypeWaterPolo
import platform.HealthKit.HKWorkoutActivityTypeWheelchairWalkPace
import platform.HealthKit.HKWorkoutActivityTypeYoga
import platform.HealthKit.HKWorkoutEvent
import platform.HealthKit.HKWorkoutEventTypeLap
import platform.HealthKit.HKWorkoutEventTypeSegment
import platform.HealthKit.HKWorkoutRoute
import platform.HealthKit.HKWorkoutRouteQuery
import platform.HealthKit.countUnit
import platform.HealthKit.degreeFahrenheitUnit
import platform.HealthKit.literUnit
import platform.HealthKit.meterUnit
import platform.HealthKit.millimeterOfMercuryUnit
import platform.HealthKit.minuteUnit
import platform.HealthKit.moleUnitWithMolarMass
import platform.HealthKit.percentUnit
import platform.HealthKit.poundUnit
import platform.HealthKit.unitDividedByUnit
import platform.HealthKit.wattUnit
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.days

// region Write
@OptIn(ExperimentalForeignApi::class)
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

        is BodyFatRecord -> {
            quantityTypeIdentifier = HKQuantityTypeIdentifierBodyFatPercentage
            quantity = HKQuantity.quantityWithUnit(
                unit = bodyFatUnit,
                doubleValue = record.percentage.value,
            )
            startDate = record.time.toNSDate()
            endDate = record.time.toNSDate()
        }

        is BodyTemperatureRecord -> {
            quantityTypeIdentifier = HKQuantityTypeIdentifierBodyTemperature
            quantity = HKQuantity.quantityWithUnit(
                unit = bodyTemperatureUnit,
                doubleValue = record.temperature.inFahrenheit,
            )
            startDate = record.time.toNSDate()
            endDate = record.time.toNSDate()
        }

        is CyclingPedalingCadenceRecord -> {
            quantityTypeIdentifier = HKQuantityTypeIdentifierCyclingCadence

            return record.samples.map { sample ->
                HKQuantitySample.quantitySampleWithType(
                    quantityType = HKQuantityType.quantityTypeForIdentifier(quantityTypeIdentifier)
                        ?: return null,
                    quantity = HKQuantity.quantityWithUnit(
                        unit = rpmUnit,
                        doubleValue = sample.revolutionsPerMinute,
                    ),
                    startDate = sample.time.toNSDate(),
                    endDate = sample.time.toNSDate(),
                    metadata = metadata,
                )
            }
        }

        is ExerciseSessionRecord -> {
            return listOf(
                HKWorkout.workoutWithActivityType(
                    workoutActivityType = record.exerciseType.toHKWorkoutActivityType(),
                    startDate = record.startTime.toNSDate(),
                    endDate = record.endTime.toNSDate(),
                    metadata = metadata,
                    workoutEvents = buildList {
                        record.segments.forEach { add(it.toHKWorkoutEvent()) }
                        record.laps.forEach { add(it.toKHWorkoutEvent()) }
                    },
                    totalDistance = null,
                    totalEnergyBurned = null,
                )
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

        is LeanBodyMassRecord -> {
            quantityTypeIdentifier = HKQuantityTypeIdentifierLeanBodyMass
            quantity = HKQuantity.quantityWithUnit(
                unit = massPoundUnit,
                doubleValue = record.mass.inPounds,
            )
            startDate = record.time.toNSDate()
            endDate = record.time.toNSDate()
        }

        is MenstruationFlowRecord -> {
            val typeIdentifier = HKObjectType
                .categoryTypeForIdentifier(HKCategoryTypeIdentifierMenstrualFlow)!!

            metadata[HKMetadataKeyMenstrualCycleStart] = false

            return listOf(
                HKCategorySample.categorySampleWithType(
                    type = typeIdentifier,
                    value = when (record.flow) {
                        MenstruationFlowRecord.Flow.Unknown -> HKCategoryValueMenstrualFlowUnspecified
                        MenstruationFlowRecord.Flow.Light -> HKCategoryValueMenstrualFlowLight
                        MenstruationFlowRecord.Flow.Medium -> HKCategoryValueMenstrualFlowMedium
                        MenstruationFlowRecord.Flow.Heavy -> HKCategoryValueMenstrualFlowHeavy
                    },
                    startDate = record.time.toNSDate(),
                    endDate = record.time.toNSDate(),
                    metadata = metadata,
                ),
            )
        }

        is MenstruationPeriodRecord -> {
            val typeIdentifier = HKObjectType
                .categoryTypeForIdentifier(HKCategoryTypeIdentifierMenstrualFlow)!!

            return List(
                record.startTime.daysUntil(record.endTime, TimeZone.currentSystemDefault()) + 1
            ) { index ->
                val metadata = metadata.toMutableMap()
                metadata[HKMetadataKeyMenstrualCycleStart] = index == 0

                HKCategorySample.categorySampleWithType(
                    type = typeIdentifier,
                    value = HKCategoryValueMenstrualFlowUnspecified,
                    startDate = record.startTime.plus(index.days).toNSDate(),
                    endDate = record.endTime.plus(index.days).toNSDate(),
                    metadata = metadata,
                )
            }
        }

        is OvulationTestRecord -> {
            val typeIdentifier = HKObjectType
                .categoryTypeForIdentifier(HKCategoryTypeIdentifierOvulationTestResult)!!

            return listOf(
                HKCategorySample.categorySampleWithType(
                    type = typeIdentifier,
                    value = when (record.result) {
                        OvulationTestRecord.Result.Inconclusive -> HKCategoryValueOvulationTestResultIndeterminate
                        OvulationTestRecord.Result.Positive -> HKCategoryValueOvulationTestResultLuteinizingHormoneSurge
                        OvulationTestRecord.Result.High -> HKCategoryValueOvulationTestResultEstrogenSurge
                        OvulationTestRecord.Result.Negative -> HKCategoryValueOvulationTestResultNegative
                        null -> HKCategoryValueOvulationTestResultIndeterminate
                    },
                    startDate = record.time.toNSDate(),
                    endDate = record.time.toNSDate(),
                    metadata = metadata,
                ),
            )
        }

        is PowerRecord -> {
            quantityTypeIdentifier = HKQuantityTypeIdentifierCyclingPower

            return record.samples.map { sample ->
                HKQuantitySample.quantitySampleWithType(
                    quantityType = HKQuantityType.quantityTypeForIdentifier(quantityTypeIdentifier)
                        ?: return null,
                    quantity = HKQuantity.quantityWithUnit(
                        unit = wattUnit,
                        doubleValue = sample.power.inWatts,
                    ),
                    startDate = sample.time.toNSDate(),
                    endDate = sample.time.toNSDate(),
                    metadata = metadata,
                )
            }
        }

        is SexualActivityRecord -> {
            val typeIdentifier = HKObjectType
                .categoryTypeForIdentifier(HKCategoryTypeIdentifierSexualActivity)!!

            metadata[HKMetadataKeySexualActivityProtectionUsed] =
                record.protection is SexualActivityRecord.Protection.Protected

            return listOf(
                HKCategorySample.categorySampleWithType(
                    type = typeIdentifier,
                    value = HKCategoryValueNotApplicable,
                    startDate = record.time.toNSDate(),
                    endDate = record.time.toNSDate(),
                    metadata = metadata,
                ),
            )
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
                unit = massPoundUnit,
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
        HKCategoryTypeIdentifierMenstrualFlow -> {
            @Suppress("RemoveRedundantCallsOfConversionMethods")
            val samples = sortedWith { a, b -> a.startDate.compare(b.startDate).toInt() }

            val flowRecords = samples.map { sample ->
                MenstruationFlowRecord(
                    time = sample.startDate.toKotlinInstant(),
                    flow = when (sample.value) {
                        HKCategoryValueMenstrualFlowLight -> MenstruationFlowRecord.Flow.Light
                        HKCategoryValueMenstrualFlowMedium -> MenstruationFlowRecord.Flow.Medium
                        HKCategoryValueMenstrualFlowHeavy -> MenstruationFlowRecord.Flow.Heavy
                        else -> MenstruationFlowRecord.Flow.Unknown
                    },
                    metadata = sample.metadata.toMetadata(),
                )
            }

            val periodRecords = mutableListOf<MenstruationPeriodRecord>()
            var periodStartedAt = samples.first().startDate.toKotlinInstant()
            val timeZone = TimeZone.currentSystemDefault()
            for (i in 1 until samples.size) {
                val sample = samples[i]

                val previousDate = samples[i - 1].startDate.toKotlinInstant().midnight()
                val currentDate = sample.startDate.toKotlinInstant().midnight()
                val days = previousDate.daysUntil(currentDate, timeZone)
                if (days > 1) {
                    periodRecords += MenstruationPeriodRecord(
                        startTime = periodStartedAt,
                        endTime = samples[i - 1].endDate.toKotlinInstant(),
                        metadata = sample.metadata.toMetadata(),
                    )
                    periodStartedAt = sample.startDate.toKotlinInstant()
                    continue
                }

                if (i == samples.size - 1) {
                    periodRecords += MenstruationPeriodRecord(
                        startTime = periodStartedAt,
                        endTime = sample.endDate.toKotlinInstant(),
                        metadata = sample.metadata.toMetadata(),
                    )
                }
            }

            flowRecords + periodRecords
        }

        HKCategoryTypeIdentifierOvulationTestResult -> {
            map { sample ->
                val time = sample.startDate.toKotlinInstant()
                val result = when (sample.value) {
                    HKCategoryValueOvulationTestResultIndeterminate -> OvulationTestRecord.Result.Inconclusive
                    HKCategoryValueOvulationTestResultLuteinizingHormoneSurge -> OvulationTestRecord.Result.Positive
                    HKCategoryValueOvulationTestResultEstrogenSurge -> OvulationTestRecord.Result.High
                    HKCategoryValueOvulationTestResultNegative -> OvulationTestRecord.Result.Negative
                    else -> OvulationTestRecord.Result.Inconclusive
                }
                OvulationTestRecord(
                    time = time,
                    result = result,
                    metadata = sample.metadata.toMetadata(),
                )
            }
        }

        HKCategoryTypeIdentifierSexualActivity -> {
            map { sample ->
                val time = sample.startDate.toKotlinInstant()
                val metadata = sample.metadata.orEmpty()
                val protection = when {
                    !metadata.containsKey(HKMetadataKeySexualActivityProtectionUsed) ->
                        SexualActivityRecord.Protection.Unknown

                    metadata.metadataBooleanTrue(HKMetadataKeySexualActivityProtectionUsed) ->
                        SexualActivityRecord.Protection.Protected

                    else ->
                        SexualActivityRecord.Protection.Unprotected
                }
                SexualActivityRecord(
                    time = time,
                    protection = protection,
                    metadata = sample.metadata.toMetadata(),
                )
            }
        }

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

internal suspend fun List<HKQuantitySample>.toHealthRecord(
    temperaturePreference: suspend () -> TemperatureRegionalPreference,
): List<HealthRecord> {
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

        HKQuantityTypeIdentifierBodyFatPercentage -> {
            map { sample ->
                BodyFatRecord(
                    time = sample.startDate.toKotlinInstant(),
                    percentage = sample.quantity.bodyFatValue,
                    metadata = sample.toMetadata(),
                )
            }
        }

        HKQuantityTypeIdentifierBodyTemperature -> {
            map { sample ->
                BodyTemperatureRecord(
                    time = sample.startDate.toKotlinInstant(),
                    temperature = sample.quantity.bodyTemperatureValue
                        .preferred(temperaturePreference()),
                    measurementLocation = null,
                    metadata = sample.toMetadata(),
                )
            }
        }

        HKQuantityTypeIdentifierCyclingCadence -> {
            val metadata = firstOrNull()?.metadata.toMetadata()
            map { sample ->
                CyclingPedalingCadenceRecord.Sample(
                    time = sample.startDate.toKotlinInstant(),
                    revolutionsPerMinute = sample.quantity.rpmValue,
                )
            }.sortedBy { it.time }
                .let { samples ->
                    listOf(
                        CyclingPedalingCadenceRecord(
                            startTime = samples.first().time,
                            endTime = samples.last().time,
                            samples = samples,
                            metadata = metadata,
                        )
                    )
                }
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

        HKQuantityTypeIdentifierHeight -> {
            map { sample ->
                HeightRecord(
                    time = sample.startDate.toKotlinInstant(),
                    height = sample.quantity.heightValue,
                    metadata = sample.toMetadata(),
                )
            }
        }

        HKQuantityTypeIdentifierLeanBodyMass -> {
            map { sample ->
                LeanBodyMassRecord(
                    time = sample.startDate.toKotlinInstant(),
                    mass = sample.quantity.massValue,
                    metadata = sample.toMetadata(),
                )
            }
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
                    weight = sample.quantity.massValue,
                    metadata = sample.toMetadata(),
                )
            }
        }

        HKQuantityTypeIdentifierCyclingPower -> {
            val metadata = firstOrNull()?.metadata.toMetadata()
            map { sample ->
                PowerRecord.Sample(
                    time = sample.startDate.toKotlinInstant(),
                    power = sample.quantity.wattValue.watts,
                )
            }.sortedBy { it.time }
                .let { samples ->
                    listOf(
                        PowerRecord(
                            startTime = samples.first().time,
                            endTime = samples.last().time,
                            samples = samples,
                            metadata = metadata,
                        )
                    )
                }
        }

        else -> emptyList()
    }
}

@Suppress("FilterIsInstanceResultIsAlwaysEmpty")
internal suspend fun List<HKWorkout>.toHealthRecords(
    healthStore: HKHealthStore
): List<HealthRecord> = withContext(Dispatchers.Default) {
    val routes = filterIsInstance<HKWorkoutRoute>()

    filterIsInstance<HKWorkout>().map { workout ->
        val route = routes.find { route ->
            route.startDate.timeIntervalSinceReferenceDate >= workout.startDate.timeIntervalSinceReferenceDate &&
                    route.endDate.timeIntervalSinceReferenceDate <= workout.endDate.timeIntervalSinceReferenceDate
        }

        val exerciseRoute = if (route != null) {
            val locations = suspendCancellableCoroutine { continuation ->
                val query = HKWorkoutRouteQuery(route) { _, result, _, _ ->
                    if (continuation.isCancelled) return@HKWorkoutRouteQuery

                    when {
                        result?.firstOrNull() is CLLocation -> {
                            @Suppress("UNCHECKED_CAST")
                            continuation.resume(result as List<CLLocation>)
                        }

                        else -> {
                            continuation.resume(null)
                        }
                    }
                }
                healthStore.executeQuery(query)
            }

            locations
                ?.map { it.toExerciseRouteLocation() }
                ?.let { ExerciseRoute(it) }
        } else {
            null
        }

        val workoutEvents = workout.workoutEvents.orEmpty()
            .filterIsInstance<HKWorkoutEvent>()

        ExerciseSessionRecord(
            startTime = workout.startDate.toKotlinInstant(),
            endTime = workout.endDate.toKotlinInstant(),
            exerciseType = workout.workoutActivityType.toExerciseType(),
            metadata = workout.metadata.toMetadata(),
            segments = workoutEvents
                .filter { it.type == HKWorkoutEventTypeSegment }
                .map { it.toExerciseSegment() },
            laps = workoutEvents
                .filter { it.type == HKWorkoutEventTypeLap }
                .map { it.toExerciseLap() },
            exerciseRoute = exerciseRoute,
        )
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

internal val HKQuantity?.bodyTemperatureValue: Temperature
    get() = Temperature.fahrenheit(
        this?.doubleValueForUnit(bodyTemperatureUnit) ?: 0.0
    )

private val bodyTemperatureUnit: HKUnit
    get() = HKUnit.degreeFahrenheitUnit()

internal fun Temperature.preferred(temperaturePreference: TemperatureRegionalPreference): Temperature =
    when (temperaturePreference) {
        TemperatureRegionalPreference.Celsius -> Temperature.celsius(inCelsius)
        TemperatureRegionalPreference.Fahrenheit -> Temperature.fahrenheit(inFahrenheit)
    }

internal val HKQuantity?.bodyFatValue: Percentage
    get() = this?.doubleValueForUnit(bodyFatUnit)?.percent ?: 0.percent

private val bodyFatUnit: HKUnit
    get() = HKUnit.percentUnit()

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

internal val HKQuantity?.lengthValue: Length
    get() = Length.meters(
        this?.doubleValueForUnit(lengthUnit) ?: 0.0
    )

internal val lengthUnit: HKUnit
    get() = HKUnit.meterUnit()

internal val HKQuantity?.massValue: Mass
    get() = Mass.pounds(
        this?.doubleValueForUnit(massPoundUnit) ?: 0.0
    )

private val massPoundUnit: HKUnit
    get() = HKUnit.poundUnit()

internal val HKQuantity?.stepsValue: Long
    get() = this?.doubleValueForUnit(stepsUnit)?.toLong() ?: 0L

private val wattUnit: HKUnit
    get() = HKUnit.wattUnit()

internal val HKQuantity?.wattValue: Double
    get() = this?.doubleValueForUnit(wattUnit) ?: 0.0

private val rpmUnit: HKUnit
    get() = HKUnit.countUnit().unitDividedByUnit(HKUnit.minuteUnit())

internal val HKQuantity?.rpmValue: Double
    get() = this?.doubleValueForUnit(rpmUnit) ?: 0.0

private val stepsUnit: HKUnit
    get() = HKUnit.countUnit()
// endregion

// region Exercise
private fun ExerciseType.toHKWorkoutActivityType(): HKWorkoutActivityType = when (this) {
    ExerciseType.Badminton -> HKWorkoutActivityTypeBadminton
    ExerciseType.Baseball -> HKWorkoutActivityTypeBaseball
    ExerciseType.Basketball -> HKWorkoutActivityTypeBasketball
    ExerciseType.Biking -> HKWorkoutActivityTypeSwimBikeRun
    ExerciseType.BikingStationary -> HKWorkoutActivityTypeSwimBikeRun
    ExerciseType.BootCamp -> HKWorkoutActivityTypeOther
    ExerciseType.Boxing -> HKWorkoutActivityTypeBoxing
    ExerciseType.Calisthenics -> HKWorkoutActivityTypeOther
    ExerciseType.Cricket -> HKWorkoutActivityTypeCricket
    ExerciseType.Dancing -> HKWorkoutActivityTypeDance
    ExerciseType.Elliptical -> HKWorkoutActivityTypeElliptical
    ExerciseType.ExerciseClass -> HKWorkoutActivityTypeOther
    ExerciseType.Fencing -> HKWorkoutActivityTypeFencing
    ExerciseType.FootballAmerican -> HKWorkoutActivityTypeAmericanFootball
    ExerciseType.FootballAustralian -> HKWorkoutActivityTypeAustralianFootball
    ExerciseType.FrisbeeDisc -> HKWorkoutActivityTypeDiscSports
    ExerciseType.Golf -> HKWorkoutActivityTypeGolf
    ExerciseType.GuidedBreathing -> HKWorkoutActivityTypeOther
    ExerciseType.Gymnastics -> HKWorkoutActivityTypeGymnastics
    ExerciseType.Handball -> HKWorkoutActivityTypeHandball
    ExerciseType.HighIntensityIntervalTraining -> HKWorkoutActivityTypeHighIntensityIntervalTraining
    ExerciseType.Hiking -> HKWorkoutActivityTypeHiking
    ExerciseType.IceHockey -> HKWorkoutActivityTypeHockey
    ExerciseType.IceSkating -> HKWorkoutActivityTypeSkatingSports
    ExerciseType.MartialArts -> HKWorkoutActivityTypeMartialArts
    ExerciseType.OtherWorkout -> HKWorkoutActivityTypeOther
    ExerciseType.Paddling -> HKWorkoutActivityTypePaddleSports
    ExerciseType.Paragliding -> HKWorkoutActivityTypeOther
    ExerciseType.Pilates -> HKWorkoutActivityTypePilates
    ExerciseType.Racquetball -> HKWorkoutActivityTypeRacquetball
    ExerciseType.RockClimbing -> HKWorkoutActivityTypeClimbing
    ExerciseType.RollerHockey -> HKWorkoutActivityTypeOther
    ExerciseType.Rowing -> HKWorkoutActivityTypeRowing
    ExerciseType.RowingMachine -> HKWorkoutActivityTypeRowing
    ExerciseType.Rugby -> HKWorkoutActivityTypeRugby
    ExerciseType.Running -> HKWorkoutActivityTypeRunning
    ExerciseType.RunningTreadmill -> HKWorkoutActivityTypeRunning
    ExerciseType.Sailing -> HKWorkoutActivityTypeSailing
    ExerciseType.ScubaDiving -> HKWorkoutActivityTypeUnderwaterDiving
    ExerciseType.Skating -> HKWorkoutActivityTypeSkatingSports
    ExerciseType.Skiing -> HKWorkoutActivityTypeDownhillSkiing
    ExerciseType.Snowboarding -> HKWorkoutActivityTypeSnowboarding
    ExerciseType.Snowshoeing -> HKWorkoutActivityTypeSnowSports
    ExerciseType.Soccer -> HKWorkoutActivityTypeSoccer
    ExerciseType.Softball -> HKWorkoutActivityTypeSoftball
    ExerciseType.Squash -> HKWorkoutActivityTypeSquash
    ExerciseType.StairClimbing -> HKWorkoutActivityTypeStairClimbing
    ExerciseType.StairClimbingMachine -> HKWorkoutActivityTypeStairClimbing
    ExerciseType.StrengthTraining -> HKWorkoutActivityTypeTraditionalStrengthTraining
    ExerciseType.Stretching -> HKWorkoutActivityTypeTraditionalStrengthTraining
    ExerciseType.Surfing -> HKWorkoutActivityTypeSurfingSports
    ExerciseType.SwimmingOpenWater -> HKWorkoutActivityTypeSwimming
    ExerciseType.SwimmingPool -> HKWorkoutActivityTypeSwimming
    ExerciseType.TableTennis -> HKWorkoutActivityTypeTableTennis
    ExerciseType.Tennis -> HKWorkoutActivityTypeTennis
    ExerciseType.Volleyball -> HKWorkoutActivityTypeVolleyball
    ExerciseType.Walking -> HKWorkoutActivityTypeWalking
    ExerciseType.WaterPolo -> HKWorkoutActivityTypeWaterPolo
    ExerciseType.Weightlifting -> HKWorkoutActivityTypeOther
    ExerciseType.Wheelchair -> HKWorkoutActivityTypeWheelchairWalkPace
    ExerciseType.Yoga -> HKWorkoutActivityTypeYoga
}

private fun HKWorkoutActivityType.toExerciseType(): ExerciseType = when (this) {
    HKWorkoutActivityTypeAmericanFootball -> ExerciseType.FootballAmerican
    HKWorkoutActivityTypeAustralianFootball -> ExerciseType.FootballAustralian
    HKWorkoutActivityTypeBadminton -> ExerciseType.Badminton
    HKWorkoutActivityTypeBaseball -> ExerciseType.Baseball
    HKWorkoutActivityTypeBasketball -> ExerciseType.Basketball
    HKWorkoutActivityTypeBoxing -> ExerciseType.Boxing
    HKWorkoutActivityTypeClimbing -> ExerciseType.RockClimbing
    HKWorkoutActivityTypeCricket -> ExerciseType.Cricket
    HKWorkoutActivityTypeDance -> ExerciseType.Dancing
    HKWorkoutActivityTypeDiscSports -> ExerciseType.FrisbeeDisc
    HKWorkoutActivityTypeDownhillSkiing -> ExerciseType.Skiing
    HKWorkoutActivityTypeElliptical -> ExerciseType.Elliptical
    HKWorkoutActivityTypeFencing -> ExerciseType.Fencing
    HKWorkoutActivityTypeGolf -> ExerciseType.Golf
    HKWorkoutActivityTypeGymnastics -> ExerciseType.Gymnastics
    HKWorkoutActivityTypeHandball -> ExerciseType.Handball
    HKWorkoutActivityTypeHighIntensityIntervalTraining -> ExerciseType.HighIntensityIntervalTraining
    HKWorkoutActivityTypeHiking -> ExerciseType.Hiking
    HKWorkoutActivityTypeHockey -> ExerciseType.IceHockey
    HKWorkoutActivityTypeMartialArts -> ExerciseType.MartialArts
    HKWorkoutActivityTypePaddleSports -> ExerciseType.Paddling
    HKWorkoutActivityTypePilates -> ExerciseType.Pilates
    HKWorkoutActivityTypeRacquetball -> ExerciseType.Racquetball
    HKWorkoutActivityTypeRowing -> ExerciseType.Rowing
    HKWorkoutActivityTypeRugby -> ExerciseType.Rugby
    HKWorkoutActivityTypeRunning -> ExerciseType.Running
    HKWorkoutActivityTypeSailing -> ExerciseType.Sailing
    HKWorkoutActivityTypeSkatingSports -> ExerciseType.Skating
    HKWorkoutActivityTypeSnowboarding -> ExerciseType.Snowboarding
    HKWorkoutActivityTypeSnowSports -> ExerciseType.Snowshoeing
    HKWorkoutActivityTypeSoccer -> ExerciseType.Soccer
    HKWorkoutActivityTypeSoftball -> ExerciseType.Softball
    HKWorkoutActivityTypeSquash -> ExerciseType.Squash
    HKWorkoutActivityTypeStairClimbing -> ExerciseType.StairClimbing
    HKWorkoutActivityTypeSurfingSports -> ExerciseType.Surfing
    HKWorkoutActivityTypeSwimBikeRun -> ExerciseType.Biking
    HKWorkoutActivityTypeSwimming -> ExerciseType.SwimmingPool
    HKWorkoutActivityTypeTableTennis -> ExerciseType.TableTennis
    HKWorkoutActivityTypeTennis -> ExerciseType.Tennis
    HKWorkoutActivityTypeTraditionalStrengthTraining -> ExerciseType.StrengthTraining
    HKWorkoutActivityTypeUnderwaterDiving -> ExerciseType.ScubaDiving
    HKWorkoutActivityTypeVolleyball -> ExerciseType.Volleyball
    HKWorkoutActivityTypeWalking -> ExerciseType.Walking
    HKWorkoutActivityTypeWaterPolo -> ExerciseType.WaterPolo
    HKWorkoutActivityTypeWheelchairWalkPace -> ExerciseType.Wheelchair
    HKWorkoutActivityTypeYoga -> ExerciseType.Yoga
    else -> ExerciseType.OtherWorkout
}

@OptIn(ExperimentalForeignApi::class)
internal fun ExerciseRoute.Location.toCLLocation(): CLLocation {
    return CLLocation(
        coordinate = CLLocationCoordinate2DMake(
            latitude = latitude,
            longitude = longitude,
        ),
        altitude = altitude?.inMeters ?: 0.0,
        horizontalAccuracy = horizontalAccuracy?.inMeters ?: 0.0,
        verticalAccuracy = verticalAccuracy?.inMeters ?: 0.0,
        timestamp = time.toNSDate(),
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun CLLocation.toExerciseRouteLocation(): ExerciseRoute.Location {
    return coordinate.useContents {
        ExerciseRoute.Location(
            latitude = latitude,
            longitude = longitude,
            altitude = Length.meters(altitude),
            horizontalAccuracy = Length.meters(horizontalAccuracy),
            verticalAccuracy = Length.meters(verticalAccuracy),
            time = timestamp.toKotlinInstant(),
        )
    }
}

private fun ExerciseLap.toKHWorkoutEvent(): HKWorkoutEvent {
    return HKWorkoutEvent.workoutEventWithType(
        type = HKWorkoutEventTypeLap,
        dateInterval = NSDateInterval(
            startDate = startTime.toNSDate(),
            endDate = endTime.toNSDate(),
        ),
        metadata = length?.let {
            mapOf(
                HKMetadataKeyLapLength to HKQuantity.quantityWithUnit(
                    unit = lengthUnit,
                    doubleValue = length.inMeters,
                )
            )
        },
    )
}

private fun HKWorkoutEvent.toExerciseLap(): ExerciseLap {
    return ExerciseLap(
        startTime = dateInterval.startDate.toKotlinInstant(),
        endTime = dateInterval.endDate.toKotlinInstant(),
        length = (metadata?.get(HKMetadataKeyLapLength) as? HKQuantity)?.lengthValue,
    )
}

private fun ExerciseSegment.toHKWorkoutEvent(): HKWorkoutEvent {
    return HKWorkoutEvent.workoutEventWithType(
        type = HKWorkoutEventTypeSegment,
        dateInterval = NSDateInterval(
            startDate = startTime.toNSDate(),
            endDate = endTime.toNSDate(),
        ),
        metadata = null,
    )
}

private fun HKWorkoutEvent.toExerciseSegment(): ExerciseSegment {
    return ExerciseSegment(
        startTime = dateInterval.startDate.toKotlinInstant(),
        endTime = dateInterval.endDate.toKotlinInstant(),
        segmentType = ExerciseSegment.Type.Unknown,
    )
}
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
            type = when {
                deviceName.orEmpty().contains("iphone", ignoreCase = true) -> {
                    DeviceType.Phone
                }

                deviceName.orEmpty().contains("watch", ignoreCase = true) -> {
                    DeviceType.Watch
                }

                else -> {
                    DeviceType.Unknown
                }
            },
            manufacturer = deviceManufacturer,
            model = deviceName,
        )
    } else {
        null
    }

    return when {
        metadata.metadataBooleanTrue(HKMetadataKeyWasUserEntered) -> {
            Metadata.manualEntry(id = id, device = device)
        }

        device != null -> {
            Metadata.autoRecorded(id = id, device = device)
        }

        else -> {
            Metadata.manualEntry(id = id, device = device)
        }
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

private fun Map<Any?, Any?>.metadataBooleanTrue(key: String): Boolean =
    this[key] == true || this[key] == "true" || this[key] == 1.0

/**
 * https://developer.apple.com/documentation/healthkit/metadata-keys
 */
// General keys
private const val HKMetadataKeyExternalUUID = "HKExternalUUID"
private const val HKMetadataKeyWasUserEntered = "HKWasUserEntered"

// Device information keys
private const val HKMetadataKeyDeviceManufacturerName = "HKDeviceManufacturerName"
private const val HKMetadataKeyDeviceName = "HKDeviceName"

// Blood glucose
private const val HKMetadataKeyBloodGlucoseMealTime = "HKBloodGlucoseMealTime"
private const val HKBloodGlucoseMealTimePreprandial = 1.0
private const val HKBloodGlucoseMealTimePostprandial = 2.0

// Sexual activity
private const val HKMetadataKeySexualActivityProtectionUsed = "HKSexualActivityProtectionUsed"
// endregion