package com.viktormykhailiv.kmp.health.aggregate

import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.BloodGlucose
import com.viktormykhailiv.kmp.health.units.BloodGlucose as BloodGlucoseUnit
import kotlin.time.Instant

/**
 * Captures the aggregated user's blood glucose.
 *
 * @param avg Average blood glucose.
 * @param min Minimum blood glucose.
 * @param max Maximum blood glucose.
 *
 * @see [BloodGlucoseUnit] for supported units.
 */
data class BloodGlucoseAggregatedRecord(
    val startTime: Instant,
    val endTime: Instant,
    val avg: BloodGlucoseUnit,
    val min: BloodGlucoseUnit,
    val max: BloodGlucoseUnit,
) : HealthAggregatedRecord {

    override val dataType: HealthDataType = BloodGlucose
}
