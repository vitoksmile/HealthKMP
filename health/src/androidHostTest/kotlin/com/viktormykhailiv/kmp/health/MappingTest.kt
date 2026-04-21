package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.records.BloodGlucoseRecord
import com.viktormykhailiv.kmp.health.records.BloodPressureRecord
import com.viktormykhailiv.kmp.health.records.BodyFatRecord
import com.viktormykhailiv.kmp.health.records.BodyTemperatureRecord
import com.viktormykhailiv.kmp.health.records.CyclingPedalingCadenceRecord
import com.viktormykhailiv.kmp.health.records.ExerciseSessionRecord
import com.viktormykhailiv.kmp.health.records.ExerciseType
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.HeightRecord
import com.viktormykhailiv.kmp.health.records.LeanBodyMassRecord
import com.viktormykhailiv.kmp.health.records.MenstruationFlowRecord
import com.viktormykhailiv.kmp.health.records.MenstruationPeriodRecord
import com.viktormykhailiv.kmp.health.records.OvulationTestRecord
import com.viktormykhailiv.kmp.health.records.PowerRecord
import com.viktormykhailiv.kmp.health.records.SexualActivityRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.region.TemperatureRegionalPreference
import com.viktormykhailiv.kmp.health.units.BloodGlucose
import com.viktormykhailiv.kmp.health.units.celsius
import com.viktormykhailiv.kmp.health.units.kilograms
import com.viktormykhailiv.kmp.health.units.meters
import com.viktormykhailiv.kmp.health.units.millimetersOfMercury
import com.viktormykhailiv.kmp.health.units.percent
import com.viktormykhailiv.kmp.health.units.watts
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Instant
import androidx.health.connect.client.records.BloodGlucoseRecord as HCBloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord as HCBloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord as HCBodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord as HCBodyTemperatureRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord as HCPedalingCadenceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord as HCExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord as HCHeartRateRecord
import androidx.health.connect.client.records.HeightRecord as HCHeightRecord
import androidx.health.connect.client.records.LeanBodyMassRecord as HCLeanBodyMassRecord
import androidx.health.connect.client.records.MenstruationFlowRecord as HCMenstruationFlowRecord
import androidx.health.connect.client.records.MenstruationPeriodRecord as HCMenstruationPeriodRecord
import androidx.health.connect.client.records.OvulationTestRecord as HCOvulationTestRecord
import androidx.health.connect.client.records.PowerRecord as HCPowerRecord
import androidx.health.connect.client.records.SexualActivityRecord as HCSexualActivityRecord
import androidx.health.connect.client.records.SleepSessionRecord as HCSleepSessionRecord
import androidx.health.connect.client.records.StepsRecord as HCStepsRecord
import androidx.health.connect.client.records.WeightRecord as HCWeightRecord

class MappingTest {

    private val time = Instant.fromEpochMilliseconds(1000)
    private val startTime = Instant.fromEpochMilliseconds(1000)
    private val endTime = Instant.fromEpochMilliseconds(2000)
    private val metadata = Metadata.unknownRecordingMethod()

    private val tempPreference = { TemperatureRegionalPreference.Celsius }

    @Test
    fun bloodGlucoseMapping() {
        val common = BloodGlucoseRecord(
            time = time,
            level = BloodGlucose.millimolesPerLiter(5.5),
            specimenSource = BloodGlucoseRecord.SpecimenSource.CapillaryBlood,
            mealType = null,
            relationToMeal = BloodGlucoseRecord.RelationToMeal.BeforeMeal,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCBloodGlucoseRecord
        assertEquals(5.5, hc.level.inMillimolesPerLiter)

        val mappedBack = hc.toHealthRecord(tempPreference) as BloodGlucoseRecord
        assertEquals(common.level, mappedBack.level)
        assertEquals(common.relationToMeal, mappedBack.relationToMeal)
    }

    @Test
    fun bloodPressureMapping() {
        val common = BloodPressureRecord(
            time = time,
            systolic = 120.millimetersOfMercury,
            diastolic = 80.millimetersOfMercury,
            bodyPosition = BloodPressureRecord.BodyPosition.SittingDown,
            measurementLocation = BloodPressureRecord.MeasurementLocation.LeftUpperArm,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCBloodPressureRecord
        assertEquals(120.0, hc.systolic.inMillimetersOfMercury)
        assertEquals(80.0, hc.diastolic.inMillimetersOfMercury)

        val mappedBack = hc.toHealthRecord(tempPreference) as BloodPressureRecord
        assertEquals(common.systolic, mappedBack.systolic)
        assertEquals(common.diastolic, mappedBack.diastolic)
    }

    @Test
    fun bodyFatMapping() {
        val common = BodyFatRecord(time = time, percentage = 20.percent, metadata = metadata)
        val hc = common.toHCRecord(tempPreference) as HCBodyFatRecord
        assertEquals(20.0, hc.percentage.value)

        val mappedBack = hc.toHealthRecord(tempPreference) as BodyFatRecord
        assertEquals(common.percentage, mappedBack.percentage)
    }

    @Test
    fun bodyTemperatureMapping() {
        val common = BodyTemperatureRecord(
            time = time,
            temperature = 36.6.celsius,
            measurementLocation = BodyTemperatureRecord.MeasurementLocation.Mouth,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCBodyTemperatureRecord
        assertEquals(36.6, hc.temperature.inCelsius)

        val mappedBack = hc.toHealthRecord(tempPreference) as BodyTemperatureRecord
        assertEquals(common.temperature.inCelsius, mappedBack.temperature.inCelsius, 0.1)
    }

    @Test
    fun exerciseSessionMapping() {
        val common = ExerciseSessionRecord(
            startTime = startTime,
            endTime = endTime,
            exerciseType = ExerciseType.Running,
            title = "Morning Run",
            notes = "Feeling good",
            exerciseRoute = null,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCExerciseSessionRecord
        assertEquals(HCExerciseSessionRecord.EXERCISE_TYPE_RUNNING, hc.exerciseType)
        assertEquals("Morning Run", hc.title)

        val mappedBack = hc.toHealthRecord(tempPreference) as ExerciseSessionRecord
        assertEquals(common.exerciseType, mappedBack.exerciseType)
        assertEquals(common.title, mappedBack.title)
    }

    @Test
    fun heartRateMapping() {
        val samples = listOf(HeartRateRecord.Sample(time = time, beatsPerMinute = 70))
        val common = HeartRateRecord(
            startTime = startTime,
            endTime = endTime,
            samples = samples,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCHeartRateRecord
        assertEquals(70, hc.samples.first().beatsPerMinute)

        val mappedBack = hc.toHealthRecord(tempPreference) as HeartRateRecord
        assertEquals(
            common.samples.first().beatsPerMinute,
            mappedBack.samples.first().beatsPerMinute
        )
    }

    @Test
    fun heightMapping() {
        val common = HeightRecord(time = time, height = 1.8.meters, metadata = metadata)
        val hc = common.toHCRecord(tempPreference) as HCHeightRecord
        assertEquals(1.8, hc.height.inMeters)

        val mappedBack = hc.toHealthRecord(tempPreference) as HeightRecord
        assertEquals(common.height, mappedBack.height)
    }

    @Test
    fun leanBodyMassMapping() {
        val common = LeanBodyMassRecord(time = time, mass = 60.kilograms, metadata = metadata)
        val hc = common.toHCRecord(tempPreference) as HCLeanBodyMassRecord
        assertEquals(60.0, hc.mass.inKilograms)

        val mappedBack = hc.toHealthRecord(tempPreference) as LeanBodyMassRecord
        assertEquals(common.mass, mappedBack.mass)
    }

    @Test
    fun menstruationFlowMapping() {
        val common = MenstruationFlowRecord(
            time = time,
            flow = MenstruationFlowRecord.Flow.Medium,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCMenstruationFlowRecord
        assertEquals(HCMenstruationFlowRecord.FLOW_MEDIUM, hc.flow)

        val mappedBack = hc.toHealthRecord(tempPreference) as MenstruationFlowRecord
        assertEquals(common.flow, mappedBack.flow)
    }

    @Test
    fun menstruationPeriodMapping() {
        val common =
            MenstruationPeriodRecord(startTime = startTime, endTime = endTime, metadata = metadata)
        val hc = common.toHCRecord(tempPreference) as HCMenstruationPeriodRecord

        val mappedBack = hc.toHealthRecord(tempPreference) as MenstruationPeriodRecord
        assertEquals(common.startTime, mappedBack.startTime)
        assertEquals(common.endTime, mappedBack.endTime)
    }

    @Test
    fun ovulationTestMapping() {
        val common = OvulationTestRecord(
            time = time,
            result = OvulationTestRecord.Result.Positive,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCOvulationTestRecord
        assertEquals(HCOvulationTestRecord.RESULT_POSITIVE, hc.result)

        val mappedBack = hc.toHealthRecord(tempPreference) as OvulationTestRecord
        assertEquals(common.result, mappedBack.result)
    }

    @Test
    fun sexualActivityMapping() {
        val common = SexualActivityRecord(
            time = time,
            protection = SexualActivityRecord.Protection.Protected,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCSexualActivityRecord
        assertEquals(HCSexualActivityRecord.PROTECTION_USED_PROTECTED, hc.protectionUsed)

        val mappedBack = hc.toHealthRecord(tempPreference) as SexualActivityRecord
        assertEquals(common.protection, mappedBack.protection)
    }

    @Test
    fun sleepSessionMapping() {
        val stages = listOf(
            SleepSessionRecord.Stage(
                startTime = startTime,
                endTime = endTime,
                type = SleepStageType.Deep
            )
        )
        val common = SleepSessionRecord(
            startTime = startTime,
            endTime = endTime,
            stages = stages,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCSleepSessionRecord
        assertEquals(HCSleepSessionRecord.STAGE_TYPE_DEEP, hc.stages.first().stage)

        val mappedBack = hc.toHealthRecord(tempPreference) as SleepSessionRecord
        assertEquals(common.stages.first().type, mappedBack.stages.first().type)
    }

    @Test
    fun stepsMapping() {
        val common =
            StepsRecord(startTime = startTime, endTime = endTime, count = 100, metadata = metadata)
        val hc = common.toHCRecord(tempPreference) as HCStepsRecord
        assertEquals(100, hc.count)

        val mappedBack = hc.toHealthRecord(tempPreference) as StepsRecord
        assertEquals(common.count, mappedBack.count)
    }

    @Test
    fun weightMapping() {
        val common = WeightRecord(time = time, weight = 70.kilograms, metadata = metadata)
        val hc = common.toHCRecord(tempPreference) as HCWeightRecord
        assertEquals(70.0, hc.weight.inKilograms)

        val mappedBack = hc.toHealthRecord(tempPreference) as WeightRecord
        assertEquals(common.weight, mappedBack.weight)
    }

    @Test
    fun powerMapping() {
        val samples = listOf(PowerRecord.Sample(time = time, power = 200.watts))
        val common = PowerRecord(
            startTime = startTime,
            endTime = endTime,
            samples = samples,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCPowerRecord
        assertEquals(200.0, hc.samples.first().power.inWatts)

        val mappedBack = hc.toHealthRecord(tempPreference) as PowerRecord
        assertEquals(common.samples.first().power, mappedBack.samples.first().power)
    }

    @Test
    fun cyclingPedalingCadenceMapping() {
        val samples =
            listOf(CyclingPedalingCadenceRecord.Sample(time = time, revolutionsPerMinute = 90.0))
        val common = CyclingPedalingCadenceRecord(
            startTime = startTime,
            endTime = endTime,
            samples = samples,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCPedalingCadenceRecord
        assertEquals(90.0, hc.samples.first().revolutionsPerMinute)

        val mappedBack = hc.toHealthRecord(tempPreference) as CyclingPedalingCadenceRecord
        assertEquals(
            common.samples.first().revolutionsPerMinute,
            mappedBack.samples.first().revolutionsPerMinute
        )
    }
}
