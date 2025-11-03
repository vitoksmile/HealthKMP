package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.records.BloodGlucoseRecord
import com.viktormykhailiv.kmp.health.records.BloodPressureRecord
import com.viktormykhailiv.kmp.health.records.BodyFatRecord
import com.viktormykhailiv.kmp.health.records.BodyTemperatureRecord
import com.viktormykhailiv.kmp.health.records.ExerciseLap
import com.viktormykhailiv.kmp.health.records.ExerciseRoute
import com.viktormykhailiv.kmp.health.records.ExerciseSegment
import com.viktormykhailiv.kmp.health.records.ExerciseSessionRecord
import com.viktormykhailiv.kmp.health.records.ExerciseType
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.HeightRecord
import com.viktormykhailiv.kmp.health.records.LeanBodyMassRecord
import com.viktormykhailiv.kmp.health.records.MealType
import com.viktormykhailiv.kmp.health.records.CyclingPedalingCadenceRecord
import com.viktormykhailiv.kmp.health.records.PowerRecord
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
import com.viktormykhailiv.kmp.health.units.Power
import com.viktormykhailiv.kmp.health.units.Pressure
import com.viktormykhailiv.kmp.health.units.Temperature
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import androidx.health.connect.client.records.BloodGlucoseRecord as HCBloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord as HCBloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord as HCBodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureMeasurementLocation as HCBodyTemperatureMeasurementLocation
import androidx.health.connect.client.records.BodyTemperatureRecord as HCBodyTemperatureRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord as HCPedalingCadenceRecord
import androidx.health.connect.client.records.ExerciseLap as HCExerciseLap
import androidx.health.connect.client.records.ExerciseRoute as HCExerciseRoute
import androidx.health.connect.client.records.ExerciseRouteResult as HCExerciseRouteResult
import androidx.health.connect.client.records.ExerciseSegment as HCExerciseSegment
import androidx.health.connect.client.records.ExerciseSessionRecord as HCExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord as HCHeartRateRecord
import androidx.health.connect.client.records.HeightRecord as HCHeightRecord
import androidx.health.connect.client.records.LeanBodyMassRecord as HCLeanBodyMassRecord
import androidx.health.connect.client.records.MealType as HCMealType
import androidx.health.connect.client.records.PowerRecord as HCPowerRecord
import androidx.health.connect.client.records.Record as HCRecord
import androidx.health.connect.client.records.SleepSessionRecord as HCSleepSessionRecord
import androidx.health.connect.client.records.StepsRecord as HCStepsRecord
import androidx.health.connect.client.records.WeightRecord as HCWeightRecord
import androidx.health.connect.client.records.metadata.Device as HCDevice
import androidx.health.connect.client.records.metadata.Metadata as HCMetadata
import androidx.health.connect.client.units.BloodGlucose as HCBloodGlucose
import androidx.health.connect.client.units.Length as HCLength
import androidx.health.connect.client.units.Mass as HCMass
import androidx.health.connect.client.units.Percentage as HCPercentage
import androidx.health.connect.client.units.Power as HCPower
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

    is ExerciseSessionRecord -> HCExerciseSessionRecord(
        startTime = record.startTime.toJavaInstant(),
        endTime = record.endTime.toJavaInstant(),
        startZoneOffset = null,
        endZoneOffset = null,
        exerciseType = when (exerciseType) {
            ExerciseType.OtherWorkout -> HCExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT
            ExerciseType.Badminton -> HCExerciseSessionRecord.EXERCISE_TYPE_BADMINTON
            ExerciseType.Baseball -> HCExerciseSessionRecord.EXERCISE_TYPE_BASEBALL
            ExerciseType.Basketball -> HCExerciseSessionRecord.EXERCISE_TYPE_BASKETBALL
            ExerciseType.Biking -> HCExerciseSessionRecord.EXERCISE_TYPE_BIKING
            ExerciseType.BikingStationary -> HCExerciseSessionRecord.EXERCISE_TYPE_BIKING_STATIONARY
            ExerciseType.BootCamp -> HCExerciseSessionRecord.EXERCISE_TYPE_BOOT_CAMP
            ExerciseType.Boxing -> HCExerciseSessionRecord.EXERCISE_TYPE_BOXING
            ExerciseType.Calisthenics -> HCExerciseSessionRecord.EXERCISE_TYPE_CALISTHENICS
            ExerciseType.Cricket -> HCExerciseSessionRecord.EXERCISE_TYPE_CRICKET
            ExerciseType.Dancing -> HCExerciseSessionRecord.EXERCISE_TYPE_DANCING
            ExerciseType.Elliptical -> HCExerciseSessionRecord.EXERCISE_TYPE_ELLIPTICAL
            ExerciseType.ExerciseClass -> HCExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS
            ExerciseType.Fencing -> HCExerciseSessionRecord.EXERCISE_TYPE_FENCING
            ExerciseType.FootballAmerican -> HCExerciseSessionRecord.EXERCISE_TYPE_FOOTBALL_AMERICAN
            ExerciseType.FootballAustralian -> HCExerciseSessionRecord.EXERCISE_TYPE_FOOTBALL_AUSTRALIAN
            ExerciseType.FrisbeeDisc -> HCExerciseSessionRecord.EXERCISE_TYPE_FRISBEE_DISC
            ExerciseType.Golf -> HCExerciseSessionRecord.EXERCISE_TYPE_GOLF
            ExerciseType.GuidedBreathing -> HCExerciseSessionRecord.EXERCISE_TYPE_GUIDED_BREATHING
            ExerciseType.Gymnastics -> HCExerciseSessionRecord.EXERCISE_TYPE_GYMNASTICS
            ExerciseType.Handball -> HCExerciseSessionRecord.EXERCISE_TYPE_HANDBALL
            ExerciseType.HighIntensityIntervalTraining -> HCExerciseSessionRecord.EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING
            ExerciseType.Hiking -> HCExerciseSessionRecord.EXERCISE_TYPE_HIKING
            ExerciseType.IceHockey -> HCExerciseSessionRecord.EXERCISE_TYPE_ICE_HOCKEY
            ExerciseType.IceSkating -> HCExerciseSessionRecord.EXERCISE_TYPE_ICE_SKATING
            ExerciseType.MartialArts -> HCExerciseSessionRecord.EXERCISE_TYPE_MARTIAL_ARTS
            ExerciseType.Paddling -> HCExerciseSessionRecord.EXERCISE_TYPE_PADDLING
            ExerciseType.Paragliding -> HCExerciseSessionRecord.EXERCISE_TYPE_PARAGLIDING
            ExerciseType.Pilates -> HCExerciseSessionRecord.EXERCISE_TYPE_PILATES
            ExerciseType.Racquetball -> HCExerciseSessionRecord.EXERCISE_TYPE_RACQUETBALL
            ExerciseType.RockClimbing -> HCExerciseSessionRecord.EXERCISE_TYPE_ROCK_CLIMBING
            ExerciseType.RollerHockey -> HCExerciseSessionRecord.EXERCISE_TYPE_ROLLER_HOCKEY
            ExerciseType.Rowing -> HCExerciseSessionRecord.EXERCISE_TYPE_ROWING
            ExerciseType.RowingMachine -> HCExerciseSessionRecord.EXERCISE_TYPE_ROWING_MACHINE
            ExerciseType.Rugby -> HCExerciseSessionRecord.EXERCISE_TYPE_RUGBY
            ExerciseType.Running -> HCExerciseSessionRecord.EXERCISE_TYPE_RUNNING
            ExerciseType.RunningTreadmill -> HCExerciseSessionRecord.EXERCISE_TYPE_RUNNING_TREADMILL
            ExerciseType.Sailing -> HCExerciseSessionRecord.EXERCISE_TYPE_SAILING
            ExerciseType.ScubaDiving -> HCExerciseSessionRecord.EXERCISE_TYPE_SCUBA_DIVING
            ExerciseType.Skating -> HCExerciseSessionRecord.EXERCISE_TYPE_SKATING
            ExerciseType.Skiing -> HCExerciseSessionRecord.EXERCISE_TYPE_SKIING
            ExerciseType.Snowboarding -> HCExerciseSessionRecord.EXERCISE_TYPE_SNOWBOARDING
            ExerciseType.Snowshoeing -> HCExerciseSessionRecord.EXERCISE_TYPE_SNOWSHOEING
            ExerciseType.Soccer -> HCExerciseSessionRecord.EXERCISE_TYPE_SOCCER
            ExerciseType.Softball -> HCExerciseSessionRecord.EXERCISE_TYPE_SOFTBALL
            ExerciseType.Squash -> HCExerciseSessionRecord.EXERCISE_TYPE_SQUASH
            ExerciseType.StairClimbing -> HCExerciseSessionRecord.EXERCISE_TYPE_STAIR_CLIMBING
            ExerciseType.StairClimbingMachine -> HCExerciseSessionRecord.EXERCISE_TYPE_STAIR_CLIMBING_MACHINE
            ExerciseType.StrengthTraining -> HCExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING
            ExerciseType.Stretching -> HCExerciseSessionRecord.EXERCISE_TYPE_STRETCHING
            ExerciseType.Surfing -> HCExerciseSessionRecord.EXERCISE_TYPE_SURFING
            ExerciseType.SwimmingOpenWater -> HCExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_OPEN_WATER
            ExerciseType.SwimmingPool -> HCExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL
            ExerciseType.TableTennis -> HCExerciseSessionRecord.EXERCISE_TYPE_TABLE_TENNIS
            ExerciseType.Tennis -> HCExerciseSessionRecord.EXERCISE_TYPE_TENNIS
            ExerciseType.Volleyball -> HCExerciseSessionRecord.EXERCISE_TYPE_VOLLEYBALL
            ExerciseType.Walking -> HCExerciseSessionRecord.EXERCISE_TYPE_WALKING
            ExerciseType.WaterPolo -> HCExerciseSessionRecord.EXERCISE_TYPE_WATER_POLO
            ExerciseType.Weightlifting -> HCExerciseSessionRecord.EXERCISE_TYPE_WEIGHTLIFTING
            ExerciseType.Wheelchair -> HCExerciseSessionRecord.EXERCISE_TYPE_WHEELCHAIR
            ExerciseType.Yoga -> HCExerciseSessionRecord.EXERCISE_TYPE_YOGA
        },
        title = record.title,
        notes = record.notes,
        segments = record.segments.map { segment ->
            HCExerciseSegment(
                startTime = segment.startTime.toJavaInstant(),
                endTime = segment.endTime.toJavaInstant(),
                segmentType = when (segment.segmentType) {
                    ExerciseSegment.Type.Unknown -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_UNKNOWN
                    ExerciseSegment.Type.ArmCurl -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_ARM_CURL
                    ExerciseSegment.Type.BackExtension -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_BACK_EXTENSION
                    ExerciseSegment.Type.BallSlam -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_BALL_SLAM
                    ExerciseSegment.Type.BarbellShoulderPress -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_BARBELL_SHOULDER_PRESS
                    ExerciseSegment.Type.BenchPress -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_BENCH_PRESS
                    ExerciseSegment.Type.BenchSitUp -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_BENCH_SIT_UP
                    ExerciseSegment.Type.Biking -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_BIKING
                    ExerciseSegment.Type.BikingStationary -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_BIKING_STATIONARY
                    ExerciseSegment.Type.Burpee -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_BURPEE
                    ExerciseSegment.Type.Crunch -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_CRUNCH
                    ExerciseSegment.Type.Deadlift -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DEADLIFT
                    ExerciseSegment.Type.DoubleArmTricepsExtension -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DOUBLE_ARM_TRICEPS_EXTENSION
                    ExerciseSegment.Type.DumbbellCurlLeftArm -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DUMBBELL_CURL_LEFT_ARM
                    ExerciseSegment.Type.DumbbellCurlRightArm -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DUMBBELL_CURL_RIGHT_ARM
                    ExerciseSegment.Type.DumbbellFrontRaise -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DUMBBELL_FRONT_RAISE
                    ExerciseSegment.Type.DumbbellLateralRaise -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DUMBBELL_LATERAL_RAISE
                    ExerciseSegment.Type.DumbbellRow -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DUMBBELL_ROW
                    ExerciseSegment.Type.DumbbellTricepsExtensionLeftArm -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DUMBBELL_TRICEPS_EXTENSION_LEFT_ARM
                    ExerciseSegment.Type.DumbbellTricepsExtensionRightArm -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DUMBBELL_TRICEPS_EXTENSION_RIGHT_ARM
                    ExerciseSegment.Type.DumbbellTricepsExtensionTwoArm -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DUMBBELL_TRICEPS_EXTENSION_TWO_ARM
                    ExerciseSegment.Type.Elliptical -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_ELLIPTICAL
                    ExerciseSegment.Type.ForwardTwist -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_FORWARD_TWIST
                    ExerciseSegment.Type.FrontRaise -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_FRONT_RAISE
                    ExerciseSegment.Type.HighIntensityIntervalTraining -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING
                    ExerciseSegment.Type.HipThrust -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_HIP_THRUST
                    ExerciseSegment.Type.HulaHoop -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_HULA_HOOP
                    ExerciseSegment.Type.JumpingJack -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_JUMPING_JACK
                    ExerciseSegment.Type.JumpRope -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_JUMP_ROPE
                    ExerciseSegment.Type.KettlebellSwing -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_KETTLEBELL_SWING
                    ExerciseSegment.Type.LateralRaise -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_LATERAL_RAISE
                    ExerciseSegment.Type.LatPullDown -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_LAT_PULL_DOWN
                    ExerciseSegment.Type.LegCurl -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_LEG_CURL
                    ExerciseSegment.Type.LegExtension -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_LEG_EXTENSION
                    ExerciseSegment.Type.LegPress -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_LEG_PRESS
                    ExerciseSegment.Type.LegRaise -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_LEG_RAISE
                    ExerciseSegment.Type.Lunge -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_LUNGE
                    ExerciseSegment.Type.MountainClimber -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_MOUNTAIN_CLIMBER
                    ExerciseSegment.Type.OtherWorkout -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_OTHER_WORKOUT
                    ExerciseSegment.Type.Pause -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_PAUSE
                    ExerciseSegment.Type.Pilates -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_PILATES
                    ExerciseSegment.Type.Plank -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_PLANK
                    ExerciseSegment.Type.PullUp -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_PULL_UP
                    ExerciseSegment.Type.Punch -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_PUNCH
                    ExerciseSegment.Type.Rest -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_REST
                    ExerciseSegment.Type.RowingMachine -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_ROWING_MACHINE
                    ExerciseSegment.Type.Running -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_RUNNING
                    ExerciseSegment.Type.RunningTreadmill -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_RUNNING_TREADMILL
                    ExerciseSegment.Type.ShoulderPress -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SHOULDER_PRESS
                    ExerciseSegment.Type.SingleArmTricepsExtension -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SINGLE_ARM_TRICEPS_EXTENSION
                    ExerciseSegment.Type.SitUp -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SIT_UP
                    ExerciseSegment.Type.Squat -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SQUAT
                    ExerciseSegment.Type.StairClimbing -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_STAIR_CLIMBING
                    ExerciseSegment.Type.StairClimbingMachine -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_STAIR_CLIMBING_MACHINE
                    ExerciseSegment.Type.Stretching -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_STRETCHING
                    ExerciseSegment.Type.SwimmingBackstroke -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_BACKSTROKE
                    ExerciseSegment.Type.SwimmingBreaststroke -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_BREASTSTROKE
                    ExerciseSegment.Type.SwimmingButterfly -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_BUTTERFLY
                    ExerciseSegment.Type.SwimmingFreestyle -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_FREESTYLE
                    ExerciseSegment.Type.SwimmingMixed -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_MIXED
                    ExerciseSegment.Type.SwimmingOpenWater -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_OPEN_WATER
                    ExerciseSegment.Type.SwimmingOther -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_OTHER
                    ExerciseSegment.Type.SwimmingPool -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_POOL
                    ExerciseSegment.Type.UpperTwist -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_UPPER_TWIST
                    ExerciseSegment.Type.Walking -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_WALKING
                    ExerciseSegment.Type.Weightlifting -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_WEIGHTLIFTING
                    ExerciseSegment.Type.Wheelchair -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_WHEELCHAIR
                    ExerciseSegment.Type.Yoga -> HCExerciseSegment.EXERCISE_SEGMENT_TYPE_YOGA
                },
                repetitions = segment.repetitions,
            )
        },
        laps = record.laps.map { lap ->
            HCExerciseLap(
                startTime = lap.startTime.toJavaInstant(),
                endTime = lap.endTime.toJavaInstant(),
                length = lap.length?.toHCLength(),
            )
        },
        exerciseRoute = record.exerciseRoute?.let {
            HCExerciseRoute(route = emptyList())
        },
        plannedExerciseSessionId = record.plannedExerciseSessionId,
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

    is PowerRecord -> HCPowerRecord(
        startTime = record.startTime.toJavaInstant(),
        endTime = record.endTime.toJavaInstant(),
        startZoneOffset = null,
        endZoneOffset = null,
        samples = record.samples.map { sample ->
            HCPowerRecord.Sample(
                time = sample.time.toJavaInstant(),
                power = sample.power.toHCPower(),
            )
        },
        metadata = record.metadata.toHCMetadata(),
    )

    is CyclingPedalingCadenceRecord -> HCPedalingCadenceRecord(
        startTime = record.startTime.toJavaInstant(),
        endTime = record.endTime.toJavaInstant(),
        startZoneOffset = null,
        endZoneOffset = null,
        samples = record.samples.map { sample ->
            HCPedalingCadenceRecord.Sample(
                time = sample.time.toJavaInstant(),
                revolutionsPerMinute = sample.revolutionsPerMinute,
            )
        },
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

    is HCPedalingCadenceRecord -> CyclingPedalingCadenceRecord(
        startTime = record.startTime.toKotlinInstant(),
        endTime = record.endTime.toKotlinInstant(),
        samples = record.samples.map { sample ->
            CyclingPedalingCadenceRecord.Sample(
                time = sample.time.toKotlinInstant(),
                revolutionsPerMinute = sample.revolutionsPerMinute,
            )
        },
        metadata = record.metadata.toMetadata(),
    )
    
    is HCExerciseSessionRecord -> ExerciseSessionRecord(
        startTime = record.startTime.toKotlinInstant(),
        endTime = record.endTime.toKotlinInstant(),
        exerciseType = when (record.exerciseType) {
            HCExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT -> ExerciseType.OtherWorkout
            HCExerciseSessionRecord.EXERCISE_TYPE_BADMINTON -> ExerciseType.Badminton
            HCExerciseSessionRecord.EXERCISE_TYPE_BASEBALL -> ExerciseType.Baseball
            HCExerciseSessionRecord.EXERCISE_TYPE_BASKETBALL -> ExerciseType.Basketball
            HCExerciseSessionRecord.EXERCISE_TYPE_BIKING -> ExerciseType.Biking
            HCExerciseSessionRecord.EXERCISE_TYPE_BIKING_STATIONARY -> ExerciseType.BikingStationary
            HCExerciseSessionRecord.EXERCISE_TYPE_BOOT_CAMP -> ExerciseType.BootCamp
            HCExerciseSessionRecord.EXERCISE_TYPE_BOXING -> ExerciseType.Boxing
            HCExerciseSessionRecord.EXERCISE_TYPE_CALISTHENICS -> ExerciseType.Calisthenics
            HCExerciseSessionRecord.EXERCISE_TYPE_CRICKET -> ExerciseType.Cricket
            HCExerciseSessionRecord.EXERCISE_TYPE_DANCING -> ExerciseType.Dancing
            HCExerciseSessionRecord.EXERCISE_TYPE_ELLIPTICAL -> ExerciseType.Elliptical
            HCExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS -> ExerciseType.ExerciseClass
            HCExerciseSessionRecord.EXERCISE_TYPE_FENCING -> ExerciseType.Fencing
            HCExerciseSessionRecord.EXERCISE_TYPE_FOOTBALL_AMERICAN -> ExerciseType.FootballAmerican
            HCExerciseSessionRecord.EXERCISE_TYPE_FOOTBALL_AUSTRALIAN -> ExerciseType.FootballAustralian
            HCExerciseSessionRecord.EXERCISE_TYPE_FRISBEE_DISC -> ExerciseType.FrisbeeDisc
            HCExerciseSessionRecord.EXERCISE_TYPE_GOLF -> ExerciseType.Golf
            HCExerciseSessionRecord.EXERCISE_TYPE_GUIDED_BREATHING -> ExerciseType.GuidedBreathing
            HCExerciseSessionRecord.EXERCISE_TYPE_GYMNASTICS -> ExerciseType.Gymnastics
            HCExerciseSessionRecord.EXERCISE_TYPE_HANDBALL -> ExerciseType.Handball
            HCExerciseSessionRecord.EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING -> ExerciseType.HighIntensityIntervalTraining
            HCExerciseSessionRecord.EXERCISE_TYPE_HIKING -> ExerciseType.Hiking
            HCExerciseSessionRecord.EXERCISE_TYPE_ICE_HOCKEY -> ExerciseType.IceHockey
            HCExerciseSessionRecord.EXERCISE_TYPE_ICE_SKATING -> ExerciseType.IceSkating
            HCExerciseSessionRecord.EXERCISE_TYPE_MARTIAL_ARTS -> ExerciseType.MartialArts
            HCExerciseSessionRecord.EXERCISE_TYPE_PADDLING -> ExerciseType.Paddling
            HCExerciseSessionRecord.EXERCISE_TYPE_PARAGLIDING -> ExerciseType.Paragliding
            HCExerciseSessionRecord.EXERCISE_TYPE_PILATES -> ExerciseType.Pilates
            HCExerciseSessionRecord.EXERCISE_TYPE_RACQUETBALL -> ExerciseType.Racquetball
            HCExerciseSessionRecord.EXERCISE_TYPE_ROCK_CLIMBING -> ExerciseType.RockClimbing
            HCExerciseSessionRecord.EXERCISE_TYPE_ROLLER_HOCKEY -> ExerciseType.RollerHockey
            HCExerciseSessionRecord.EXERCISE_TYPE_ROWING -> ExerciseType.Rowing
            HCExerciseSessionRecord.EXERCISE_TYPE_ROWING_MACHINE -> ExerciseType.RowingMachine
            HCExerciseSessionRecord.EXERCISE_TYPE_RUGBY -> ExerciseType.Rugby
            HCExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> ExerciseType.Running
            HCExerciseSessionRecord.EXERCISE_TYPE_RUNNING_TREADMILL -> ExerciseType.RunningTreadmill
            HCExerciseSessionRecord.EXERCISE_TYPE_SAILING -> ExerciseType.Sailing
            HCExerciseSessionRecord.EXERCISE_TYPE_SCUBA_DIVING -> ExerciseType.ScubaDiving
            HCExerciseSessionRecord.EXERCISE_TYPE_SKATING -> ExerciseType.Skating
            HCExerciseSessionRecord.EXERCISE_TYPE_SKIING -> ExerciseType.Skiing
            HCExerciseSessionRecord.EXERCISE_TYPE_SNOWBOARDING -> ExerciseType.Snowboarding
            HCExerciseSessionRecord.EXERCISE_TYPE_SNOWSHOEING -> ExerciseType.Snowshoeing
            HCExerciseSessionRecord.EXERCISE_TYPE_SOCCER -> ExerciseType.Soccer
            HCExerciseSessionRecord.EXERCISE_TYPE_SOFTBALL -> ExerciseType.Softball
            HCExerciseSessionRecord.EXERCISE_TYPE_SQUASH -> ExerciseType.Squash
            HCExerciseSessionRecord.EXERCISE_TYPE_STAIR_CLIMBING -> ExerciseType.StairClimbing
            HCExerciseSessionRecord.EXERCISE_TYPE_STAIR_CLIMBING_MACHINE -> ExerciseType.StairClimbingMachine
            HCExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING -> ExerciseType.StrengthTraining
            HCExerciseSessionRecord.EXERCISE_TYPE_STRETCHING -> ExerciseType.Stretching
            HCExerciseSessionRecord.EXERCISE_TYPE_SURFING -> ExerciseType.Surfing
            HCExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_OPEN_WATER -> ExerciseType.SwimmingOpenWater
            HCExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL -> ExerciseType.SwimmingPool
            HCExerciseSessionRecord.EXERCISE_TYPE_TABLE_TENNIS -> ExerciseType.TableTennis
            HCExerciseSessionRecord.EXERCISE_TYPE_TENNIS -> ExerciseType.Tennis
            HCExerciseSessionRecord.EXERCISE_TYPE_VOLLEYBALL -> ExerciseType.Volleyball
            HCExerciseSessionRecord.EXERCISE_TYPE_WALKING -> ExerciseType.Walking
            HCExerciseSessionRecord.EXERCISE_TYPE_WATER_POLO -> ExerciseType.WaterPolo
            HCExerciseSessionRecord.EXERCISE_TYPE_WEIGHTLIFTING -> ExerciseType.Weightlifting
            HCExerciseSessionRecord.EXERCISE_TYPE_WHEELCHAIR -> ExerciseType.Wheelchair
            HCExerciseSessionRecord.EXERCISE_TYPE_YOGA -> ExerciseType.Yoga
            else -> ExerciseType.OtherWorkout
        },
        title = record.title,
        notes = record.notes,
        segments = record.segments.map { segment ->
            ExerciseSegment(
                startTime = segment.startTime.toKotlinInstant(),
                endTime = segment.endTime.toKotlinInstant(),
                segmentType = when (segment.segmentType) {
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_UNKNOWN -> ExerciseSegment.Type.Unknown
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_ARM_CURL -> ExerciseSegment.Type.ArmCurl
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_BACK_EXTENSION -> ExerciseSegment.Type.BackExtension
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_BALL_SLAM -> ExerciseSegment.Type.BallSlam
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_BARBELL_SHOULDER_PRESS -> ExerciseSegment.Type.BarbellShoulderPress
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_BENCH_PRESS -> ExerciseSegment.Type.BenchPress
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_BENCH_SIT_UP -> ExerciseSegment.Type.BenchSitUp
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_BIKING -> ExerciseSegment.Type.Biking
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_BIKING_STATIONARY -> ExerciseSegment.Type.BikingStationary
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_BURPEE -> ExerciseSegment.Type.Burpee
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_CRUNCH -> ExerciseSegment.Type.Crunch
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DEADLIFT -> ExerciseSegment.Type.Deadlift
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DOUBLE_ARM_TRICEPS_EXTENSION -> ExerciseSegment.Type.DoubleArmTricepsExtension
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DUMBBELL_CURL_LEFT_ARM -> ExerciseSegment.Type.DumbbellCurlLeftArm
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DUMBBELL_CURL_RIGHT_ARM -> ExerciseSegment.Type.DumbbellCurlRightArm
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DUMBBELL_FRONT_RAISE -> ExerciseSegment.Type.DumbbellFrontRaise
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DUMBBELL_LATERAL_RAISE -> ExerciseSegment.Type.DumbbellLateralRaise
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DUMBBELL_ROW -> ExerciseSegment.Type.DumbbellRow
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DUMBBELL_TRICEPS_EXTENSION_LEFT_ARM -> ExerciseSegment.Type.DumbbellTricepsExtensionLeftArm
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DUMBBELL_TRICEPS_EXTENSION_RIGHT_ARM -> ExerciseSegment.Type.DumbbellTricepsExtensionRightArm
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_DUMBBELL_TRICEPS_EXTENSION_TWO_ARM -> ExerciseSegment.Type.DumbbellTricepsExtensionTwoArm
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_ELLIPTICAL -> ExerciseSegment.Type.Elliptical
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_FORWARD_TWIST -> ExerciseSegment.Type.ForwardTwist
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_FRONT_RAISE -> ExerciseSegment.Type.FrontRaise
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING -> ExerciseSegment.Type.HighIntensityIntervalTraining
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_HIP_THRUST -> ExerciseSegment.Type.HipThrust
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_HULA_HOOP -> ExerciseSegment.Type.HulaHoop
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_JUMPING_JACK -> ExerciseSegment.Type.JumpingJack
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_JUMP_ROPE -> ExerciseSegment.Type.JumpRope
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_KETTLEBELL_SWING -> ExerciseSegment.Type.KettlebellSwing
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_LATERAL_RAISE -> ExerciseSegment.Type.LateralRaise
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_LAT_PULL_DOWN -> ExerciseSegment.Type.LatPullDown
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_LEG_CURL -> ExerciseSegment.Type.LegCurl
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_LEG_EXTENSION -> ExerciseSegment.Type.LegExtension
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_LEG_PRESS -> ExerciseSegment.Type.LegPress
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_LEG_RAISE -> ExerciseSegment.Type.LegRaise
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_LUNGE -> ExerciseSegment.Type.Lunge
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_MOUNTAIN_CLIMBER -> ExerciseSegment.Type.MountainClimber
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_OTHER_WORKOUT -> ExerciseSegment.Type.OtherWorkout
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_PAUSE -> ExerciseSegment.Type.Pause
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_PILATES -> ExerciseSegment.Type.Pilates
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_PLANK -> ExerciseSegment.Type.Plank
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_PULL_UP -> ExerciseSegment.Type.PullUp
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_PUNCH -> ExerciseSegment.Type.Punch
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_REST -> ExerciseSegment.Type.Rest
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_ROWING_MACHINE -> ExerciseSegment.Type.RowingMachine
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_RUNNING -> ExerciseSegment.Type.Running
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_RUNNING_TREADMILL -> ExerciseSegment.Type.RunningTreadmill
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SHOULDER_PRESS -> ExerciseSegment.Type.ShoulderPress
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SINGLE_ARM_TRICEPS_EXTENSION -> ExerciseSegment.Type.SingleArmTricepsExtension
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SIT_UP -> ExerciseSegment.Type.SitUp
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SQUAT -> ExerciseSegment.Type.Squat
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_STAIR_CLIMBING -> ExerciseSegment.Type.StairClimbing
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_STAIR_CLIMBING_MACHINE -> ExerciseSegment.Type.StairClimbingMachine
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_STRETCHING -> ExerciseSegment.Type.Stretching
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_BACKSTROKE -> ExerciseSegment.Type.SwimmingBackstroke
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_BREASTSTROKE -> ExerciseSegment.Type.SwimmingBreaststroke
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_BUTTERFLY -> ExerciseSegment.Type.SwimmingButterfly
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_FREESTYLE -> ExerciseSegment.Type.SwimmingFreestyle
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_MIXED -> ExerciseSegment.Type.SwimmingMixed
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_OPEN_WATER -> ExerciseSegment.Type.SwimmingOpenWater
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_OTHER -> ExerciseSegment.Type.SwimmingOther
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_POOL -> ExerciseSegment.Type.SwimmingPool
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_UPPER_TWIST -> ExerciseSegment.Type.UpperTwist
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_WALKING -> ExerciseSegment.Type.Walking
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_WEIGHTLIFTING -> ExerciseSegment.Type.Weightlifting
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_WHEELCHAIR -> ExerciseSegment.Type.Wheelchair
                    HCExerciseSegment.EXERCISE_SEGMENT_TYPE_YOGA -> ExerciseSegment.Type.Yoga
                    else -> ExerciseSegment.Type.Unknown
                },
                repetitions = segment.repetitions,
            )
        },
        laps = record.laps.map { lap ->
            ExerciseLap(
                startTime = lap.startTime.toKotlinInstant(),
                endTime = lap.endTime.toKotlinInstant(),
                length = lap.length?.toLength(),
            )
        },
        exerciseRoute = when (val result = record.exerciseRouteResult) {
            is HCExerciseRouteResult.Data -> ExerciseRoute(
                route = result.exerciseRoute.route.map { location ->
                    ExerciseRoute.Location(
                        time = location.time.toKotlinInstant(),
                        latitude = location.latitude,
                        longitude = location.longitude,
                        horizontalAccuracy = location.horizontalAccuracy?.toLength(),
                        verticalAccuracy = location.verticalAccuracy?.toLength(),
                        altitude = location.altitude?.toLength(),
                    )
                },
            )

            else -> null
        },
        plannedExerciseSessionId = record.plannedExerciseSessionId,
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

    is HCPowerRecord -> PowerRecord(
        startTime = record.startTime.toKotlinInstant(),
        endTime = record.endTime.toKotlinInstant(),
        samples = record.samples.map { sample ->
            PowerRecord.Sample(
                time = sample.time.toKotlinInstant(),
                power = sample.power.toPower(),
            )
        },
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

internal fun Power.toHCPower(): HCPower =
    HCPower.watts(inWatts)

internal fun HCPower.toPower(): Power =
    Power.watts(inWatts)

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
