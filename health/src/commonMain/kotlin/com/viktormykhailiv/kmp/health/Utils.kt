package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds

internal fun <T : Comparable<T>> T.requireNotLess(other: T, name: String) {
    require(this >= other) { "$name must not be less than $other, currently $this." }
}

internal fun <T : Comparable<T>> T.requireNotMore(other: T, name: String) {
    require(this <= other) { "$name must not be more than $other, currently $this." }
}

internal fun requireNonNegative(value: Long, name: String) {
    require(value >= 0) { "$name must not be negative" }
}

internal fun requireNonNegative(value: Double, name: String) {
    require(value >= 0.0) { "$name must not be negative" }
}

internal fun List<SleepSessionRecord.Stage>.groupByRecords(metadata: Metadata): List<SleepSessionRecord> {
    if (isEmpty()) return emptyList()

    var lastStage: SleepSessionRecord.Stage? = null
    val sleepStages = mutableListOf<SleepSessionRecord.Stage>()
    val records = mutableListOf<SleepSessionRecord>()
    this.sortedBy { it.startTime }.forEach { stage ->
        if (
            stage.startTime isSameAs lastStage?.endTime ||
            lastStage == null
        ) {
            // The same session
            sleepStages.add(stage)
        } else {
            // New session
            if (sleepStages.isNotEmpty()) {
                records.add(
                    SleepSessionRecord(
                        startTime = sleepStages.first().startTime,
                        endTime = sleepStages.last().endTime,
                        stages = sleepStages.toList(),
                        metadata = metadata,
                    )
                )
            }
            sleepStages.clear()
            sleepStages.add(stage)
        }
        lastStage = stage
    }
    if (sleepStages.isNotEmpty() && records.isEmpty()) {
        // All stages are from the same session
        records.add(
            SleepSessionRecord(
                startTime = sleepStages.first().startTime,
                endTime = sleepStages.last().endTime,
                stages = sleepStages.toList(),
                metadata = metadata,
            )
        )
    }

    return records
}

private infix fun Instant.isSameAs(other: Instant?): Boolean {
    val current = this.removeMsOffset()
    return current == (other?.removeMsOffset() ?: Instant.DISTANT_PAST) ||
            current.minus(1.seconds) == (other?.removeMsOffset() ?: Instant.DISTANT_PAST)
}

/**
 *  Google Fit sets 999ms for endTime and 0ms for startTime,
 *  remove milliseconds offset to compare only by hh:mm:ss.
 */
private fun Instant.removeMsOffset(): Instant {
    return Instant.fromEpochSeconds(epochSeconds)
}
