package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.IntervalRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import kotlin.time.Instant

/**
 * Captures the user's sleep length and its stages. Each record represents a time interval for a
 * full sleep session.
 *
 * All sleep stage time intervals should fall within the sleep session interval. Time intervals for
 * stages don't need to be continuous but shouldn't overlap.
 */
data class SleepSessionRecord(
    override val startTime: Instant,
    override val endTime: Instant,
    val stages: List<Stage>,
    override val metadata: Metadata,
) : IntervalRecord {

    override val dataType: HealthDataType = Sleep

    init {
        require(startTime <= endTime) { "startTime must be before endTime." }
        if (stages.isNotEmpty()) {
            val sortedStages = stages.sortedWith { a, b -> a.startTime.compareTo(b.startTime) }
            for (i in 0 until sortedStages.lastIndex) {
                require(sortedStages[i].endTime <= sortedStages[i + 1].startTime)
            }
            // check all stages are within parent session duration
            require(sortedStages.first().startTime >= startTime)
            require(sortedStages.last().endTime <= endTime)
        }
    }

    /**
     * Captures the sleep stage the user entered during a sleep session.
     *
     * @see SleepSessionRecord
     */
    data class Stage(
        val startTime: Instant,
        val endTime: Instant,
        val type: SleepStageType,
    )
}

sealed interface SleepStageType {

    /**
     * Use this type if the stage of sleep is unknown.
     */
    data object Unknown : SleepStageType

    /**
     * The user is awake and either known to be in bed, or it is unknown whether they are in bed
     * or not.
     */
    data object Awake : SleepStageType

    /**
     * The user is awake and in bed.
     */
    data object AwakeInBed : SleepStageType

    /**
     * The user is asleep but the particular stage of sleep (light, deep or REM) is unknown.
     */
    data object Sleeping : SleepStageType

    /**
     * The user is out of bed and assumed to be awake.
     */
    data object OutOfBed : SleepStageType

    /**
     * The user is in a light sleep stage.
     */
    data object Light : SleepStageType

    /**
     * The user is in a deep sleep stage.
     */
    data object Deep : SleepStageType

    /**
     * The user is in a REM sleep stage.
     */
    data object REM : SleepStageType
}
