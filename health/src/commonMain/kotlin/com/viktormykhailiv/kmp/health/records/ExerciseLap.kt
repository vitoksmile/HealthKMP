package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.units.Length
import kotlin.time.Instant

/**
 * Captures the time of a lap within an exercise session.
 *
 * Each lap contains the start and end time and optional [Length] of the lap (e.g. pool length
 * while swimming or a track lap while running). There may or may not be direct correlation with
 * [ExerciseSegment] start and end times, e.g. [ExerciseSessionRecord] of type running without any
 * segments can be divided into laps of different lengths.
 *
 * @param length Lap length in [Length] unit. Optional field. Valid range: 0-1000000 meters.
 *
 * @see ExerciseSessionRecord
 */
data class ExerciseLap(
    val startTime: Instant,
    val endTime: Instant,
    val length: Length? = null,
) {

    init {
        require(startTime < endTime) { "startTime must be before endTime." }
        if (length != null) {
            require(length.inMeters in 0.0..1000000.0) { "length valid range: 0-1000000." }
        }
    }
}
