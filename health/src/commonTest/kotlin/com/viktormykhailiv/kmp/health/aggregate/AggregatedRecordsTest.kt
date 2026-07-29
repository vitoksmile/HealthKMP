package com.viktormykhailiv.kmp.health.aggregate

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.units.BloodGlucose
import com.viktormykhailiv.kmp.health.units.celsius
import com.viktormykhailiv.kmp.health.units.kilocalories
import com.viktormykhailiv.kmp.health.units.kilograms
import com.viktormykhailiv.kmp.health.units.meters
import com.viktormykhailiv.kmp.health.units.millimetersOfMercury
import com.viktormykhailiv.kmp.health.units.percent
import com.viktormykhailiv.kmp.health.units.watts
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class AggregatedRecordsTest {

    private val startTime = Instant.fromEpochMilliseconds(1000)
    private val endTime = Instant.fromEpochMilliseconds(2000)

    @Test
    fun testAggregatedRecordsDataType() {
        val activeEnergy = ActiveEnergyBurnedAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            energy = 100.kilocalories,
        )
        assertEquals(HealthDataType.ActiveEnergyBurned, activeEnergy.dataType)

        val bloodGlucose = BloodGlucoseAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            avg = BloodGlucose.millimolesPerLiter(5.0),
            min = BloodGlucose.millimolesPerLiter(4.0),
            max = BloodGlucose.millimolesPerLiter(6.0),
        )
        assertEquals(HealthDataType.BloodGlucose, bloodGlucose.dataType)

        val pressureAgg = BloodPressureAggregatedRecord.AggregatedRecord(
            avg = 120.millimetersOfMercury,
            min = 110.millimetersOfMercury,
            max = 130.millimetersOfMercury,
        )
        val bloodPressure = BloodPressureAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            systolic = pressureAgg,
            diastolic = pressureAgg,
        )
        assertEquals(HealthDataType.BloodPressure, bloodPressure.dataType)

        val bodyFat = BodyFatAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            avg = 20.percent,
            min = 18.percent,
            max = 22.percent,
        )
        assertEquals(HealthDataType.BodyFat, bodyFat.dataType)

        val bodyTemp = BodyTemperatureAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            avg = 36.6.celsius,
            min = 36.celsius,
            max = 37.celsius,
        )
        assertEquals(HealthDataType.BodyTemperature, bodyTemp.dataType)

        val cadence = CyclingPedalingCadenceAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            avg = 80.0,
            min = 60.0,
            max = 100.0,
        )
        assertEquals(HealthDataType.CyclingPedalingCadence, cadence.dataType)

        val distance = DistanceAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            distance = 1000.meters,
        )
        assertEquals(HealthDataType.Distance, distance.dataType)

        val heartRate = HeartRateAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            avg = 70L,
            min = 60L,
            max = 80L,
        )
        assertEquals(HealthDataType.HeartRate, heartRate.dataType)

        val height = HeightAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            avg = 1.80.meters,
            min = 1.80.meters,
            max = 1.80.meters,
        )
        assertEquals(HealthDataType.Height, height.dataType)

        val leanBodyMass = LeanBodyMassAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            avg = 70.kilograms,
            min = 70.kilograms,
            max = 70.kilograms,
        )
        assertEquals(HealthDataType.LeanBodyMass, leanBodyMass.dataType)

        val power = PowerAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            avg = 200.watts,
            min = 150.watts,
            max = 250.watts,
        )
        assertEquals(HealthDataType.Power, power.dataType)

        val sleep = SleepAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            totalDuration = 8.hours,
        )
        assertEquals(HealthDataType.Sleep, sleep.dataType)

        val steps = StepsAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            count = 10000,
        )
        assertEquals(HealthDataType.Steps, steps.dataType)

        val weight = WeightAggregatedRecord(
            startTime = startTime,
            endTime = endTime,
            avg = 75.kilograms,
            min = 75.kilograms,
            max = 75.kilograms,
        )
        assertEquals(HealthDataType.Weight, weight.dataType)
    }
}
