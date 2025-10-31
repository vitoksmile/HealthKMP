package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.CyclingPedalingCadence
import com.viktormykhailiv.kmp.health.SeriesRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import kotlin.time.Instant

/**
 * Captures the user's cycling pedaling cadence. Each record represents a series of measurements.
 */
data class CyclingPedalingCadenceRecord(
    override val startTime: Instant,
    override val endTime: Instant,
    override val samples: List<Sample>,
    override val metadata: Metadata,
) : SeriesRecord<CyclingPedalingCadenceRecord.Sample> {

    override val dataType: HealthDataType = CyclingPedalingCadence

    init {
        require(startTime <= endTime) { "startTime must be before endTime." }
    }

    /**
     * Represents a single measurement of the cycling pedaling cadence.
     *
     * @param time The point in time when the measurement was taken.
     * @param revolutionsPerMinute Cycling revolutions per minute. Valid range: 0-10000.
     * @see CyclingPedalingCadenceRecord
     */
    data class Sample(
        val time: Instant,
        val revolutionsPerMinute: Double,
    ) {
        init {
            require(revolutionsPerMinute >= 0 && revolutionsPerMinute <= 10000) { "revolutionsPerMinute must be 0-10000." }
        }
    }
}
