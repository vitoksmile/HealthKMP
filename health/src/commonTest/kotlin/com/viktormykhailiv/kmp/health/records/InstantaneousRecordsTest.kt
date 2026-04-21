package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.units.BloodGlucose
import com.viktormykhailiv.kmp.health.units.celsius
import com.viktormykhailiv.kmp.health.units.kilograms
import com.viktormykhailiv.kmp.health.units.meters
import com.viktormykhailiv.kmp.health.units.millimetersOfMercury
import com.viktormykhailiv.kmp.health.units.percent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class InstantaneousRecordsTest {

    private val time = Instant.fromEpochMilliseconds(1000)
    private val metadata = Metadata.unknownRecordingMethod()

    @Test
    fun weightRecord_validation() {
        val record = WeightRecord(time = time, weight = 70.kilograms, metadata = metadata)
        assertEquals(70.kilograms, record.weight)

        assertFailsWith<IllegalArgumentException> {
            WeightRecord(time = time, weight = (-1).kilograms, metadata = metadata)
        }
        assertFailsWith<IllegalArgumentException> {
            WeightRecord(time = time, weight = 1001.kilograms, metadata = metadata)
        }
    }

    @Test
    fun heightRecord_validation() {
        val record = HeightRecord(time = time, height = 1.8.meters, metadata = metadata)
        assertEquals(1.8.meters, record.height)

        assertFailsWith<IllegalArgumentException> {
            HeightRecord(time = time, height = (-0.1).meters, metadata = metadata)
        }
        assertFailsWith<IllegalArgumentException> {
            HeightRecord(time = time, height = 3.1.meters, metadata = metadata)
        }
    }

    @Test
    fun bodyFatRecord_validation() {
        val record = BodyFatRecord(time = time, percentage = 20.percent, metadata = metadata)
        assertEquals(20.percent, record.percentage)

        assertFailsWith<IllegalArgumentException> {
            BodyFatRecord(time = time, percentage = (-1).percent, metadata = metadata)
        }
        assertFailsWith<IllegalArgumentException> {
            BodyFatRecord(time = time, percentage = 101.percent, metadata = metadata)
        }
    }

    @Test
    fun leanBodyMassRecord_validation() {
        val record = LeanBodyMassRecord(time = time, mass = 60.kilograms, metadata = metadata)
        assertEquals(60.kilograms, record.mass)

        assertFailsWith<IllegalArgumentException> {
            LeanBodyMassRecord(time = time, mass = (-1).kilograms, metadata = metadata)
        }
        assertFailsWith<IllegalArgumentException> {
            LeanBodyMassRecord(time = time, mass = 1001.kilograms, metadata = metadata)
        }
    }

    @Test
    fun bodyTemperatureRecord_validation() {
        val record = BodyTemperatureRecord(
            time = time,
            temperature = 36.6.celsius,
            measurementLocation = BodyTemperatureRecord.MeasurementLocation.Mouth,
            metadata = metadata
        )
        assertEquals(36.6.celsius, record.temperature)
        assertEquals(BodyTemperatureRecord.MeasurementLocation.Mouth, record.measurementLocation)

        assertFailsWith<IllegalArgumentException> {
            BodyTemperatureRecord(
                time = time,
                temperature = (-1).celsius,
                measurementLocation = null,
                metadata = metadata
            )
        }
        assertFailsWith<IllegalArgumentException> {
            BodyTemperatureRecord(
                time = time,
                temperature = 101.celsius,
                measurementLocation = null,
                metadata = metadata
            )
        }
    }

    @Test
    fun bloodGlucoseRecord_validation() {
        val record = BloodGlucoseRecord(
            time = time,
            level = BloodGlucose.millimolesPerLiter(5.5),
            specimenSource = null,
            mealType = null,
            relationToMeal = null,
            metadata = metadata
        )
        assertEquals(BloodGlucose.millimolesPerLiter(5.5), record.level)

        assertFailsWith<IllegalArgumentException> {
            BloodGlucoseRecord(
                time = time,
                level = BloodGlucose.millimolesPerLiter(-0.1),
                specimenSource = null,
                mealType = null,
                relationToMeal = null,
                metadata = metadata
            )
        }
        assertFailsWith<IllegalArgumentException> {
            BloodGlucoseRecord(
                time = time,
                level = BloodGlucose.millimolesPerLiter(51.0),
                specimenSource = null,
                mealType = null,
                relationToMeal = null,
                metadata = metadata
            )
        }
    }

    @Test
    fun bloodPressureRecord_validation() {
        val record = BloodPressureRecord(
            time = time,
            systolic = 120.millimetersOfMercury,
            diastolic = 80.millimetersOfMercury,
            bodyPosition = BloodPressureRecord.BodyPosition.SittingDown,
            measurementLocation = BloodPressureRecord.MeasurementLocation.LeftUpperArm,
            metadata = metadata
        )
        assertEquals(120.millimetersOfMercury, record.systolic)
        assertEquals(80.millimetersOfMercury, record.diastolic)

        assertFailsWith<IllegalArgumentException> {
            BloodPressureRecord(
                time = time,
                systolic = 19.millimetersOfMercury,
                diastolic = 80.millimetersOfMercury,
                bodyPosition = null,
                measurementLocation = null,
                metadata = metadata
            )
        }
        assertFailsWith<IllegalArgumentException> {
            BloodPressureRecord(
                time = time,
                systolic = 120.millimetersOfMercury,
                diastolic = 9.millimetersOfMercury,
                bodyPosition = null,
                measurementLocation = null,
                metadata = metadata
            )
        }
    }
}
