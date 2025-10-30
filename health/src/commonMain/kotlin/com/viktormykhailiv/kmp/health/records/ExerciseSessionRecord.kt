package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.Exercise
import com.viktormykhailiv.kmp.health.IntervalRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import kotlin.time.Instant

/**
 * Captures any exercise a user does. This can be common fitness exercise like running or different
 * sports.
 *
 * Each record needs a start time and end time. Records don't need to be back-to-back or directly
 * after each other, there can be gaps in between.
 *
 * @param exerciseType Type of exercise (e.g. walking, swimming).
 * @param title Title of the session.
 * @param notes Additional notes for the session.
 * @param segments [ExerciseSegment]s of the session. Optional field. Time in segments should be
 * within the parent session, and should not overlap with each other.
 * @param laps [ExerciseLap]s of the session. Optional field. Time in laps should be within the
 * parent session, and should not overlap with each other.
 * @param exerciseRoute Location data points of [ExerciseRoute] should be within the parent session,
 * and should be before the end time of the session.
 * @param plannedExerciseSessionId The planned exercise session this workout was based upon.
 */
data class ExerciseSessionRecord(
    override val startTime: Instant,
    override val endTime: Instant,
    val exerciseType: ExerciseType,
    val title: String? = null,
    val notes: String? = null,
    val segments: List<ExerciseSegment> = emptyList(),
    val laps: List<ExerciseLap> = emptyList(),
    val exerciseRoute: ExerciseRoute?,
    val plannedExerciseSessionId: String? = null,
    override val metadata: Metadata
) : IntervalRecord {

    override val dataType: HealthDataType = Exercise()

    init {
        require(startTime < endTime) { "startTime must be before endTime." }
        if (segments.isNotEmpty()) {
            var sortedSegments = segments.sortedWith { a, b -> a.startTime.compareTo(b.startTime) }
            for (i in 0 until sortedSegments.lastIndex) {
                require(sortedSegments[i].endTime <= sortedSegments[i + 1].startTime) {
                    "segments can not overlap."
                }
            }
            // check all segments are within parent session duration
            require(sortedSegments.first().startTime >= startTime) {
                "segments can not be out of parent time range."
            }
            require(sortedSegments.last().endTime <= endTime) {
                "segments can not be out of parent time range."
            }
        }
        if (laps.isNotEmpty()) {
            val sortedLaps = laps.sortedWith { a, b -> a.startTime.compareTo(b.startTime) }
            for (i in 0 until sortedLaps.lastIndex) {
                require(sortedLaps[i].endTime <= sortedLaps[i + 1].startTime) {
                    "laps can not overlap."
                }
            }
            // check all laps are within parent session duration
            require(sortedLaps.first().startTime >= startTime) {
                "laps can not be out of parent time range."
            }
            require(sortedLaps.last().endTime <= endTime) {
                "laps can not be out of parent time range."
            }
        }
        if (
            exerciseRoute != null &&
            exerciseRoute.route.isNotEmpty()
        ) {
            val route = exerciseRoute.route
            val minTime = route.minBy { it.time }.time
            val maxTime = route.maxBy { it.time }.time
            require(!(minTime < startTime && maxTime < endTime)) {
                "route can not be out of parent time range."
            }
        }
    }

}
