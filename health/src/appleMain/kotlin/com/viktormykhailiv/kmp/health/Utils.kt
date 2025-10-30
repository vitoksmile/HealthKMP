package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import kotlin.time.Instant
import kotlin.time.Duration.Companion.minutes

internal data class HeartRateSampleInternal(
    val startTime: Instant,
    val endTime: Instant,
    val beatsPerMinute: Int,
)

internal fun List<HeartRateSampleInternal>.group(metadata: Metadata): List<HeartRateRecord> {
    fun List<HeartRateSampleInternal>.convert(): List<HeartRateRecord.Sample> {
        return map {
            HeartRateRecord.Sample(
                time = it.startTime,
                beatsPerMinute = it.beatsPerMinute,
            )
        }
    }

    var lastSample: HeartRateSampleInternal? = null
    val samples = mutableListOf<HeartRateSampleInternal>()
    val records = mutableListOf<HeartRateRecord>()
    this.sortedBy { it.startTime }.forEach { sample ->
        if (
            sample.startTime.minus(1.minutes) <= (lastSample?.endTime ?: Instant.DISTANT_PAST) ||
            lastSample == null
        ) {
            // The same record
            samples.add(sample)
        } else {
            // New record
            if (samples.isNotEmpty()) {
                records.add(
                    HeartRateRecord(
                        startTime = samples.first().startTime,
                        endTime = samples.last().endTime,
                        samples = samples.convert(),
                        metadata = metadata,
                    )
                )
            }
            samples.clear()
            samples.add(sample)
        }

        lastSample = sample
    }
    if (samples.isNotEmpty() && records.isEmpty()) {
        // All samples are from the same record
        records.add(
            HeartRateRecord(
                startTime = samples.first().startTime,
                endTime = samples.last().endTime,
                samples = samples.convert(),
                metadata = metadata,
            )
        )
    }

    return records
}