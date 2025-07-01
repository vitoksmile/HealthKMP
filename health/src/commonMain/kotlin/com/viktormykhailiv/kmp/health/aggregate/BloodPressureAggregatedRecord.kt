package com.viktormykhailiv.kmp.health.aggregate

import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.units.Pressure
import kotlinx.datetime.Instant

/**
 * Captures the aggregated user's blood pressure.
 *
 * @param systolic Systolic blood pressure measurement.
 * @param diastolic Diastolic blood pressure measurement.
 */
data class BloodPressureAggregatedRecord(
    val startTime: Instant,
    val endTime: Instant,
    val systolic: AggregatedRecord,
    val diastolic: AggregatedRecord,
) : HealthAggregatedRecord {

    override val dataType: HealthDataType = HeartRate

    /**
     * Captures the aggregated user's blood pressure.
     *
     * @param avg Average blood pressure.
     * @param min Minimum blood pressure.
     * @param max Maximum blood pressure.
     */
    data class AggregatedRecord(
        val avg: Pressure,
        val min: Pressure,
        val max: Pressure,
    )
}
