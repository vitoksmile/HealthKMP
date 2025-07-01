package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.BloodPressure
import com.viktormykhailiv.kmp.health.InstantaneousRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.requireNotLess
import com.viktormykhailiv.kmp.health.requireNotMore
import com.viktormykhailiv.kmp.health.units.Pressure
import com.viktormykhailiv.kmp.health.units.millimetersOfMercury
import kotlinx.datetime.Instant

/**
 * Captures the blood pressure of a user. Each record represents a single instantaneous blood
 * pressure reading.
 *
 * @param systolic Systolic blood pressure measurement, in [Pressure] unit. Required field. Valid range: 20-200 mmHg.
 * @param diastolic Diastolic blood pressure measurement, in [Pressure] unit. Required field. Valid range: 10-180 mmHg.
 * @param bodyPosition The user's body position when the measurement was taken.
 * @param measurementLocation The arm and part of the arm where the measurement was taken
 */
data class BloodPressureRecord(
    override val time: Instant,
    val systolic: Pressure,
    val diastolic: Pressure,
    val bodyPosition: BodyPosition?,
    val measurementLocation: MeasurementLocation?,
    override val metadata: Metadata,
) : InstantaneousRecord {

    override val dataType: HealthDataType = BloodPressure

    init {
        systolic.requireNotLess(other = MIN_SYSTOLIC, name = "systolic")
        systolic.requireNotMore(other = MAX_SYSTOLIC, name = "systolic")
        diastolic.requireNotLess(other = MIN_DIASTOLIC, name = "diastolic")
        diastolic.requireNotMore(other = MAX_DIASTOLIC, name = "diastolic")
    }

    /**
     * The arm and part of the arm where a blood pressure measurement was taken.
     */
    enum class MeasurementLocation {
        LeftWrist,
        RightWrist,
        LeftUpperArm,
        RightUpperArm,
    }

    /**
     * The user's body position when a health measurement is taken.
     */
    enum class BodyPosition {
        StandingUp,
        SittingDown,
        LyingDown,
        Reclining,
    }

    private companion object {
        val MIN_SYSTOLIC = 20.millimetersOfMercury
        val MAX_SYSTOLIC = 200.millimetersOfMercury
        val MIN_DIASTOLIC = 10.millimetersOfMercury
        val MAX_DIASTOLIC = 180.millimetersOfMercury
    }

}
