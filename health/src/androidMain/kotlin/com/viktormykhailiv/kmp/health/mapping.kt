package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.records.BloodGlucoseRecord
import com.viktormykhailiv.kmp.health.records.BloodPressureRecord
import com.viktormykhailiv.kmp.health.records.BodyFatRecord
import com.viktormykhailiv.kmp.health.records.BodyTemperatureRecord
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.HeightRecord
import com.viktormykhailiv.kmp.health.records.LeanBodyMassRecord
import com.viktormykhailiv.kmp.health.records.MealType
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.records.metadata.Device
import com.viktormykhailiv.kmp.health.records.metadata.DeviceType
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.region.TemperatureRegionalPreference
import com.viktormykhailiv.kmp.health.units.BloodGlucose
import com.viktormykhailiv.kmp.health.units.Length
import com.viktormykhailiv.kmp.health.units.Mass
import com.viktormykhailiv.kmp.health.units.Percentage
import com.viktormykhailiv.kmp.health.units.Pressure
import com.viktormykhailiv.kmp.health.units.Temperature
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import androidx.health.connect.client.records.metadata.Device as HCDevice
import androidx.health.connect.client.records.metadata.Metadata as HCMetadata
import androidx.health.connect.client.records.Record as HCRecord
import androidx.health.connect.client.records.BloodGlucoseRecord as HCBloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord as HCBloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord as HCBodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord as HCBodyTemperatureRecord
import androidx.health.connect.client.records.BodyTemperatureMeasurementLocation as HCBodyTemperatureMeasurementLocation
import androidx.health.connect.client.records.HeartRateRecord as HCHeartRateRecord
import androidx.health.connect.client.records.HeightRecord as HCHeightRecord
import androidx.health.connect.client.records.LeanBodyMassRecord as HCLeanBodyMassRecord
import androidx.health.connect.client.records.MealType as HCMealType
import androidx.health.connect.client.records.SleepSessionRecord as HCSleepSessionRecord
import androidx.health.connect.client.records.StepsRecord as HCStepsRecord
import androidx.health.connect.client.records.WeightRecord as HCWeightRecord
import androidx.health.connect.client.units.BloodGlucose as HCBloodGlucose
import androidx.health.connect.client.units.Length as HCLength
import androidx.health.connect.client.units.Mass as HCMass
import androidx.health.connect.client.units.Percentage as HCPercentage
import androidx.health.connect.client.units.Pressure as HCPressure
import androidx.health.connect.client.units.Temperature as HCTemperature

internal fun HealthRecord.toHCRecord(
    temperaturePreference: () -> TemperatureRegionalPreference,
): HCRecord? = when (val record = this) {
    is BloodGlucoseRecord -> HCBloodGlucoseRecord(
        time = record.time.toJavaInstant(),
        zoneOffset = null,
        level = HCBloodGlucose.millimolesPerLiter(level.inMillimolesPerLiter),
        specimenSource = when (specimenSource) {
            BloodGlucoseRecord.SpecimenSource.InterstitialFluid -> HCBloodGlucoseRecord.SPECIMEN_SOURCE_INTERSTITIAL_FLUID
            BloodGlucoseRecord.SpecimenSource.CapillaryBlood -> HCBloodGlucoseRecord.SPECIMEN_SOURCE_CAPILLARY_BLOOD
            BloodGlucoseRecord.SpecimenSource.Plasma -> HCBloodGlucoseRecord.SPECIMEN_SOURCE_PLASMA
            BloodGlucoseRecord.SpecimenSource.Serum -> HCBloodGlucoseRecord.SPECIMEN_SOURCE_SERUM
            BloodGlucoseRecord.SpecimenSource.Tears -> HCBloodGlucoseRecord.SPECIMEN_SOURCE_TEARS
            BloodGlucoseRecord.SpecimenSource.WholeBlood -> HCBloodGlucoseRecord.SPECIMEN_SOURCE_WHOLE_BLOOD
            null -> HCBloodGlucoseRecord.SPECIMEN_SOURCE_UNKNOWN
        },
        mealType = when (mealType) {
            MealType.Breakfast -> HCMealType.MEAL_TYPE_BREAKFAST
            MealType.Lunch -> HCMealType.MEAL_TYPE_LUNCH
            MealType.Dinner -> HCMealType.MEAL_TYPE_DINNER
            MealType.Snack -> HCMealType.MEAL_TYPE_SNACK
            null -> HCMealType.MEAL_TYPE_UNKNOWN
        },
        relationToMeal = when (relationToMeal) {
            BloodGlucoseRecord.RelationToMeal.General -> HCBloodGlucoseRecord.RELATION_TO_MEAL_GENERAL
            BloodGlucoseRecord.RelationToMeal.Fasting -> HCBloodGlucoseRecord.RELATION_TO_MEAL_FASTING
            BloodGlucoseRecord.RelationToMeal.BeforeMeal -> HCBloodGlucoseRecord.RELATION_TO_MEAL_BEFORE_MEAL
            BloodGlucoseRecord.RelationToMeal.AfterMeal -> HCBloodGlucoseRecord.RELATION_TO_MEAL_AFTER_MEAL
            null -> HCBloodGlucoseRecord.RELATION_TO_MEAL_UNKNOWN
        },
        metadata = record.metadata.toHCMetadata(),
    )

    is BloodPressureRecord -> HCBloodPressureRecord(
        time = record.time.toJavaInstant(),
        zoneOffset = null,
        systolic = systolic.toHCPressure(),
        diastolic = diastolic.toHCPressure(),
        bodyPosition = when (bodyPosition) {
            BloodPressureRecord.BodyPosition.StandingUp -> HCBloodPressureRecord.BODY_POSITION_STANDING_UP
            BloodPressureRecord.BodyPosition.SittingDown -> HCBloodPressureRecord.BODY_POSITION_SITTING_DOWN
            BloodPressureRecord.BodyPosition.LyingDown -> HCBloodPressureRecord.BODY_POSITION_LYING_DOWN
            BloodPressureRecord.BodyPosition.Reclining -> HCBloodPressureRecord.BODY_POSITION_RECLINING
            null -> HCBloodPressureRecord.BODY_POSITION_UNKNOWN
        },
        measurementLocation = when (measurementLocation) {
            BloodPressureRecord.MeasurementLocation.LeftWrist -> HCBloodPressureRecord.MEASUREMENT_LOCATION_LEFT_WRIST
            BloodPressureRecord.MeasurementLocation.RightWrist -> HCBloodPressureRecord.MEASUREMENT_LOCATION_RIGHT_WRIST
            BloodPressureRecord.MeasurementLocation.LeftUpperArm -> HCBloodPressureRecord.MEASUREMENT_LOCATION_LEFT_UPPER_ARM
            BloodPressureRecord.MeasurementLocation.RightUpperArm -> HCBloodPressureRecord.MEASUREMENT_LOCATION_RIGHT_UPPER_ARM
            null -> HCBloodPressureRecord.MEASUREMENT_LOCATION_UNKNOWN
        },
        metadata = record.metadata.toHCMetadata(),
    )

    is BodyFatRecord -> HCBodyFatRecord(
        time = record.time.toJavaInstant(),
        zoneOffset = null,
        percentage = HCPercentage(record.percentage.value),
        metadata = record.metadata.toHCMetadata(),
    )

    is BodyTemperatureRecord -> HCBodyTemperatureRecord(
        time = record.time.toJavaInstant(),
        zoneOffset = null,
        temperature = record.temperature.preferred(temperaturePreference()),
        measurementLocation = when (measurementLocation) {
            BodyTemperatureRecord.MeasurementLocation.Armpit -> HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_ARMPIT
            BodyTemperatureRecord.MeasurementLocation.Finger -> HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_FINGER
            BodyTemperatureRecord.MeasurementLocation.Forehead -> HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_FOREHEAD
            BodyTemperatureRecord.MeasurementLocation.Mouth -> HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_MOUTH
            BodyTemperatureRecord.MeasurementLocation.Rectum -> HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_RECTUM
            BodyTemperatureRecord.MeasurementLocation.TemporalArtery -> HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_TEMPORAL_ARTERY
            BodyTemperatureRecord.MeasurementLocation.Toe -> HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_TOE
            BodyTemperatureRecord.MeasurementLocation.Ear -> HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_EAR
            BodyTemperatureRecord.MeasurementLocation.Wrist -> HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_WRIST
            BodyTemperatureRecord.MeasurementLocation.Vagina -> HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_VAGINA
            null -> HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_UNKNOWN
        },
        metadata = record.metadata.toHCMetadata(),
    )

    is HeartRateRecord -> HCHeartRateRecord(
        startTime = record.startTime.toJavaInstant(),
        endTime = record.endTime.toJavaInstant(),
        startZoneOffset = null,
        endZoneOffset = null,
        samples = record.samples.map { sample ->
            HCHeartRateRecord.Sample(
                time = sample.time.toJavaInstant(),
                beatsPerMinute = sample.beatsPerMinute.toLong(),
            )
        },
        metadata = record.metadata.toHCMetadata(),
    )

    is HeightRecord -> HCHeightRecord(
        time = record.time.toJavaInstant(),
        zoneOffset = null,
        height = record.height.toHCLength(),
        metadata = record.metadata.toHCMetadata(),
    )

    is LeanBodyMassRecord -> HCLeanBodyMassRecord(
        time = record.time.toJavaInstant(),
        zoneOffset = null,
        mass = record.mass.toHCMass(),
        metadata = record.metadata.toHCMetadata(),
    )

    is SleepSessionRecord -> HCSleepSessionRecord(
        startTime = record.startTime.toJavaInstant(),
        endTime = record.endTime.toJavaInstant(),
        startZoneOffset = null,
        endZoneOffset = null,
        stages = record.stages.map { stage ->
            HCSleepSessionRecord.Stage(
                startTime = stage.startTime.toJavaInstant(),
                endTime = stage.endTime.toJavaInstant(),
                stage = when (stage.type) {
                    SleepStageType.Unknown -> HCSleepSessionRecord.STAGE_TYPE_UNKNOWN
                    SleepStageType.Awake -> HCSleepSessionRecord.STAGE_TYPE_AWAKE
                    SleepStageType.AwakeInBed -> HCSleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED
                    SleepStageType.Sleeping -> HCSleepSessionRecord.STAGE_TYPE_SLEEPING
                    SleepStageType.OutOfBed -> HCSleepSessionRecord.STAGE_TYPE_OUT_OF_BED
                    SleepStageType.Light -> HCSleepSessionRecord.STAGE_TYPE_LIGHT
                    SleepStageType.Deep -> HCSleepSessionRecord.STAGE_TYPE_DEEP
                    SleepStageType.REM -> HCSleepSessionRecord.STAGE_TYPE_REM
                },
            )
        },
        metadata = record.metadata.toHCMetadata(),
    )

    is StepsRecord -> HCStepsRecord(
        startTime = record.startTime.toJavaInstant(),
        endTime = record.endTime.toJavaInstant(),
        startZoneOffset = null,
        endZoneOffset = null,
        count = record.count.toLong(),
        metadata = record.metadata.toHCMetadata(),
    )

    is WeightRecord -> HCWeightRecord(
        time = record.time.toJavaInstant(),
        zoneOffset = null,
        weight = record.weight.toHCMass(),
        metadata = record.metadata.toHCMetadata(),
    )

    else -> null
}

internal fun HCRecord.toHealthRecord(
    temperaturePreference: () -> TemperatureRegionalPreference,
): HealthRecord? = when (val record = this) {
    is HCBloodGlucoseRecord -> BloodGlucoseRecord(
        time = record.time.toKotlinInstant(),
        level = BloodGlucose.millimolesPerLiter(level.inMillimolesPerLiter),
        specimenSource = when (specimenSource) {
            HCBloodGlucoseRecord.SPECIMEN_SOURCE_INTERSTITIAL_FLUID -> BloodGlucoseRecord.SpecimenSource.InterstitialFluid
            HCBloodGlucoseRecord.SPECIMEN_SOURCE_CAPILLARY_BLOOD -> BloodGlucoseRecord.SpecimenSource.CapillaryBlood
            HCBloodGlucoseRecord.SPECIMEN_SOURCE_PLASMA -> BloodGlucoseRecord.SpecimenSource.Plasma
            HCBloodGlucoseRecord.SPECIMEN_SOURCE_SERUM -> BloodGlucoseRecord.SpecimenSource.Serum
            HCBloodGlucoseRecord.SPECIMEN_SOURCE_TEARS -> BloodGlucoseRecord.SpecimenSource.Tears
            HCBloodGlucoseRecord.SPECIMEN_SOURCE_WHOLE_BLOOD -> BloodGlucoseRecord.SpecimenSource.WholeBlood
            else -> null
        },
        mealType = when (mealType) {
            HCMealType.MEAL_TYPE_BREAKFAST -> MealType.Breakfast
            HCMealType.MEAL_TYPE_LUNCH -> MealType.Lunch
            HCMealType.MEAL_TYPE_DINNER -> MealType.Dinner
            HCMealType.MEAL_TYPE_SNACK -> MealType.Snack
            else -> null
        },
        relationToMeal = when (relationToMeal) {
            HCBloodGlucoseRecord.RELATION_TO_MEAL_GENERAL -> BloodGlucoseRecord.RelationToMeal.General
            HCBloodGlucoseRecord.RELATION_TO_MEAL_FASTING -> BloodGlucoseRecord.RelationToMeal.Fasting
            HCBloodGlucoseRecord.RELATION_TO_MEAL_BEFORE_MEAL -> BloodGlucoseRecord.RelationToMeal.BeforeMeal
            HCBloodGlucoseRecord.RELATION_TO_MEAL_AFTER_MEAL -> BloodGlucoseRecord.RelationToMeal.AfterMeal
            else -> null
        },
        metadata = record.metadata.toMetadata(),
    )

    is HCBloodPressureRecord -> BloodPressureRecord(
        time = record.time.toKotlinInstant(),
        systolic = systolic.toPressure(),
        diastolic = diastolic.toPressure(),
        bodyPosition = when (bodyPosition) {
            HCBloodPressureRecord.BODY_POSITION_STANDING_UP -> BloodPressureRecord.BodyPosition.StandingUp
            HCBloodPressureRecord.BODY_POSITION_SITTING_DOWN -> BloodPressureRecord.BodyPosition.SittingDown
            HCBloodPressureRecord.BODY_POSITION_LYING_DOWN -> BloodPressureRecord.BodyPosition.LyingDown
            HCBloodPressureRecord.BODY_POSITION_RECLINING -> BloodPressureRecord.BodyPosition.Reclining
            else -> null
        },
        measurementLocation = when (measurementLocation) {
            HCBloodPressureRecord.MEASUREMENT_LOCATION_LEFT_WRIST -> BloodPressureRecord.MeasurementLocation.LeftWrist
            HCBloodPressureRecord.MEASUREMENT_LOCATION_RIGHT_WRIST -> BloodPressureRecord.MeasurementLocation.RightWrist
            HCBloodPressureRecord.MEASUREMENT_LOCATION_LEFT_UPPER_ARM -> BloodPressureRecord.MeasurementLocation.LeftUpperArm
            HCBloodPressureRecord.MEASUREMENT_LOCATION_RIGHT_UPPER_ARM -> BloodPressureRecord.MeasurementLocation.RightUpperArm
            else -> null
        },
        metadata = record.metadata.toMetadata(),
    )

    is HCBodyFatRecord -> BodyFatRecord(
        time = record.time.toKotlinInstant(),
        percentage = Percentage(record.percentage.value),
        metadata = record.metadata.toMetadata(),
    )

    is HCBodyTemperatureRecord -> BodyTemperatureRecord(
        time = record.time.toKotlinInstant(),
        temperature = record.temperature.preferred(temperaturePreference()),
        measurementLocation = when (measurementLocation) {
            HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_ARMPIT -> BodyTemperatureRecord.MeasurementLocation.Armpit
            HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_FINGER -> BodyTemperatureRecord.MeasurementLocation.Finger
            HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_FOREHEAD -> BodyTemperatureRecord.MeasurementLocation.Forehead
            HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_MOUTH -> BodyTemperatureRecord.MeasurementLocation.Mouth
            HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_RECTUM -> BodyTemperatureRecord.MeasurementLocation.Rectum
            HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_TEMPORAL_ARTERY -> BodyTemperatureRecord.MeasurementLocation.TemporalArtery
            HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_TOE -> BodyTemperatureRecord.MeasurementLocation.Toe
            HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_EAR -> BodyTemperatureRecord.MeasurementLocation.Ear
            HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_WRIST -> BodyTemperatureRecord.MeasurementLocation.Wrist
            HCBodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_VAGINA -> BodyTemperatureRecord.MeasurementLocation.Vagina
            else -> null
        },
        metadata = record.metadata.toMetadata(),
    )

    is HCHeartRateRecord -> HeartRateRecord(
        startTime = record.startTime.toKotlinInstant(),
        endTime = record.endTime.toKotlinInstant(),
        samples = record.samples.map { sample ->
            HeartRateRecord.Sample(
                time = sample.time.toKotlinInstant(),
                beatsPerMinute = sample.beatsPerMinute.toInt(),
            )
        },
        metadata = record.metadata.toMetadata(),
    )

    is HCHeightRecord -> HeightRecord(
        time = record.time.toKotlinInstant(),
        height = record.height.toLength(),
        metadata = record.metadata.toMetadata(),
    )

    is HCLeanBodyMassRecord -> LeanBodyMassRecord(
        time = record.time.toKotlinInstant(),
        mass = record.mass.toMass(),
        metadata = record.metadata.toMetadata(),
    )

    is HCSleepSessionRecord -> SleepSessionRecord(
        startTime = record.startTime.toKotlinInstant(),
        endTime = record.endTime.toKotlinInstant(),
        stages = record.stages.map { stage ->
            SleepSessionRecord.Stage(
                startTime = stage.startTime.toKotlinInstant(),
                endTime = stage.endTime.toKotlinInstant(),
                type = when (stage.stage) {
                    HCSleepSessionRecord.STAGE_TYPE_AWAKE -> SleepStageType.Awake
                    HCSleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED -> SleepStageType.AwakeInBed
                    HCSleepSessionRecord.STAGE_TYPE_SLEEPING -> SleepStageType.Sleeping
                    HCSleepSessionRecord.STAGE_TYPE_OUT_OF_BED -> SleepStageType.OutOfBed
                    HCSleepSessionRecord.STAGE_TYPE_LIGHT -> SleepStageType.Light
                    HCSleepSessionRecord.STAGE_TYPE_DEEP -> SleepStageType.Deep
                    HCSleepSessionRecord.STAGE_TYPE_REM -> SleepStageType.REM
                    else -> SleepStageType.Unknown
                }
            )
        },
        metadata = record.metadata.toMetadata(),
    )

    is HCStepsRecord -> StepsRecord(
        startTime = record.startTime.toKotlinInstant(),
        endTime = record.endTime.toKotlinInstant(),
        count = record.count.toInt(),
        metadata = record.metadata.toMetadata(),
    )

    is HCWeightRecord -> WeightRecord(
        time = record.time.toKotlinInstant(),
        weight = record.weight.toMass(),
        metadata = record.metadata.toMetadata(),
    )

    else -> null
}

private fun Metadata.toHCMetadata(): HCMetadata = when (recordingMethod) {
    is Metadata.RecordingMethod.Unknown -> HCMetadata.unknownRecordingMethod(
        device = device?.toHCDevice(),
    )

    is Metadata.RecordingMethod.ManualEntry -> HCMetadata.manualEntry(
        device = device?.toHCDevice(),
    )

    is Metadata.RecordingMethod.AutoRecorded -> device?.let {
        HCMetadata.autoRecorded(device = it.toHCDevice())
    } ?: HCMetadata.unknownRecordingMethod()
}

private fun HCMetadata.toMetadata(): Metadata = when (recordingMethod) {
    HCMetadata.RECORDING_METHOD_MANUAL_ENTRY -> Metadata.manualEntry(
        id = id,
        device = device?.toDevice(),
    )

    HCMetadata.RECORDING_METHOD_AUTOMATICALLY_RECORDED -> device?.let {
        Metadata.autoRecorded(
            id = id,
            device = it.toDevice(),
        )
    } ?: Metadata.unknownRecordingMethod(id = id)

    else -> Metadata.unknownRecordingMethod(
        id = id,
        device = device?.toDevice(),
    )
}

private fun Device.toHCDevice(): HCDevice = HCDevice(
    type = when (type) {
        is DeviceType.Unknown -> HCDevice.TYPE_UNKNOWN
        is DeviceType.Watch -> HCDevice.TYPE_WATCH
        is DeviceType.Phone -> HCDevice.TYPE_PHONE
        is DeviceType.Scale -> HCDevice.TYPE_SCALE
        is DeviceType.Ring -> HCDevice.TYPE_RING
        is DeviceType.HeadMounted -> HCDevice.TYPE_HEAD_MOUNTED
        is DeviceType.FitnessBand -> HCDevice.TYPE_FITNESS_BAND
        is DeviceType.ChestStrap -> HCDevice.TYPE_CHEST_STRAP
        is DeviceType.SmartDisplay -> HCDevice.TYPE_SMART_DISPLAY
    },
    manufacturer = manufacturer,
    model = model,
)

private fun HCDevice.toDevice(): Device = Device(
    type = when (type) {
        HCDevice.TYPE_WATCH -> DeviceType.Watch
        HCDevice.TYPE_PHONE -> DeviceType.Phone
        HCDevice.TYPE_SCALE -> DeviceType.Scale
        HCDevice.TYPE_RING -> DeviceType.Ring
        HCDevice.TYPE_HEAD_MOUNTED -> DeviceType.HeadMounted
        HCDevice.TYPE_FITNESS_BAND -> DeviceType.FitnessBand
        HCDevice.TYPE_CHEST_STRAP -> DeviceType.ChestStrap
        HCDevice.TYPE_SMART_DISPLAY -> DeviceType.SmartDisplay
        else -> DeviceType.Unknown
    },
    manufacturer = manufacturer,
    model = model,
)

private fun Mass.toHCMass(): HCMass =
    HCMass.kilograms(inKilograms)

internal fun HCMass.toMass(): Mass =
    Mass.kilograms(inKilograms)

private fun Length.toHCLength(): HCLength =
    HCLength.meters(inMeters)

internal fun HCLength.toLength(): Length =
    Length.meters(inMeters)

private fun Pressure.toHCPressure(): HCPressure =
    HCPressure.millimetersOfMercury(inMillimetersOfMercury)

internal fun HCPressure.toPressure(): Pressure =
    Pressure.millimetersOfMercury(inMillimetersOfMercury)

internal fun HCTemperature.preferred(preference: TemperatureRegionalPreference): Temperature =
    when (preference) {
        TemperatureRegionalPreference.Celsius -> Temperature.celsius(inCelsius)
        TemperatureRegionalPreference.Fahrenheit -> Temperature.fahrenheit(inFahrenheit)
    }

internal fun Temperature.preferred(preference: TemperatureRegionalPreference): HCTemperature =
    when (preference) {
        TemperatureRegionalPreference.Celsius -> HCTemperature.celsius(inCelsius)
        TemperatureRegionalPreference.Fahrenheit -> HCTemperature.fahrenheit(inFahrenheit)
    }
