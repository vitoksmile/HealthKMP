package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.InstantaneousRecord
import com.viktormykhailiv.kmp.health.SeriesRecord
import com.viktormykhailiv.kmp.health.requireNotLess
import com.viktormykhailiv.kmp.health.requireNotMore
import kotlinx.datetime.Instant

/**
 * Captures the user's heart rate.
 * Each record represents a series of measurements.
 */
class HeartRateRecord(
    override val startTime: Instant,
    override val endTime: Instant,
    override val samples: List<Sample>,
) : SeriesRecord<HeartRateRecord.Sample> {

    override val dataType: HealthDataType = HeartRate

    init {
        require(startTime <= endTime) { "startTime must be before endTime." }
    }

    /**
     * Represents a single measurement of the heart rate.
     *
     * @param time The point in time when the measurement was taken.
     * @param beatsPerMinute Heart beats per minute. Validation range: 1-300.
     *
     * @see HeartRateRecord
     */
    data class Sample(
        override val time: Instant,
        val beatsPerMinute: Int,
    ) : InstantaneousRecord {

        override val dataType: HealthDataType = HealthDataType.Steps

        init {
            beatsPerMinute.requireNotLess(other = 1, name = "beatsPerMinute")
            beatsPerMinute.requireNotMore(other = 300, name = "beatsPerMinute")
        }
    }
}