package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.units.watts
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class SeriesRecordsTest {

    private val startTime = Instant.fromEpochMilliseconds(1000)
    private val endTime = Instant.fromEpochMilliseconds(5000)
    private val metadata = Metadata.unknownRecordingMethod()

    @Test
    fun heartRateRecord_validation() {
        val samples = listOf(
            HeartRateRecord.Sample(time = Instant.fromEpochMilliseconds(2000), beatsPerMinute = 70),
            HeartRateRecord.Sample(time = Instant.fromEpochMilliseconds(3000), beatsPerMinute = 75)
        )
        val record = HeartRateRecord(
            startTime = startTime,
            endTime = endTime,
            samples = samples,
            metadata = metadata
        )
        assertEquals(samples, record.samples)

        assertFailsWith<IllegalArgumentException> {
            HeartRateRecord.Sample(time = startTime, beatsPerMinute = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            HeartRateRecord.Sample(time = startTime, beatsPerMinute = 301)
        }
    }

    @Test
    fun cyclingPedalingCadenceRecord_validation() {
        val samples = listOf(
            CyclingPedalingCadenceRecord.Sample(
                time = Instant.fromEpochMilliseconds(2000),
                revolutionsPerMinute = 90.0
            ),
            CyclingPedalingCadenceRecord.Sample(
                time = Instant.fromEpochMilliseconds(3000),
                revolutionsPerMinute = 95.0
            )
        )
        val record = CyclingPedalingCadenceRecord(
            startTime = startTime,
            endTime = endTime,
            samples = samples,
            metadata = metadata
        )
        assertEquals(samples, record.samples)

        assertFailsWith<IllegalArgumentException> {
            CyclingPedalingCadenceRecord.Sample(time = startTime, revolutionsPerMinute = -1.0)
        }
        assertFailsWith<IllegalArgumentException> {
            CyclingPedalingCadenceRecord.Sample(time = startTime, revolutionsPerMinute = 10001.0)
        }
    }

    @Test
    fun powerRecord_validation() {
        val samples = listOf(
            PowerRecord.Sample(time = Instant.fromEpochMilliseconds(2000), power = 200.watts),
            PowerRecord.Sample(time = Instant.fromEpochMilliseconds(3000), power = 250.watts)
        )
        val record = PowerRecord(
            startTime = startTime,
            endTime = endTime,
            samples = samples,
            metadata = metadata
        )
        assertEquals(samples, record.samples)

        assertFailsWith<IllegalArgumentException> {
            PowerRecord.Sample(time = startTime, power = (-1).watts)
        }
        assertFailsWith<IllegalArgumentException> {
            PowerRecord.Sample(time = startTime, power = 100001.watts)
        }
    }

    @Test
    fun sleepSessionRecord_validation() {
        val stages = listOf(
            SleepSessionRecord.Stage(
                startTime = Instant.fromEpochMilliseconds(1500),
                endTime = Instant.fromEpochMilliseconds(2500),
                type = SleepStageType.Light
            ),
            SleepSessionRecord.Stage(
                startTime = Instant.fromEpochMilliseconds(2500),
                endTime = Instant.fromEpochMilliseconds(3500),
                type = SleepStageType.Deep
            )
        )
        val record = SleepSessionRecord(
            startTime = startTime,
            endTime = endTime,
            stages = stages,
            metadata = metadata
        )
        assertEquals(stages, record.stages)

        // Overlapping stages
        assertFailsWith<IllegalArgumentException> {
            SleepSessionRecord(
                startTime = startTime,
                endTime = endTime,
                stages = listOf(
                    SleepSessionRecord.Stage(
                        startTime = Instant.fromEpochMilliseconds(1500),
                        endTime = Instant.fromEpochMilliseconds(3000),
                        type = SleepStageType.Light
                    ),
                    SleepSessionRecord.Stage(
                        startTime = Instant.fromEpochMilliseconds(2500),
                        endTime = Instant.fromEpochMilliseconds(3500),
                        type = SleepStageType.Deep
                    )
                ),
                metadata = metadata
            )
        }

        // Stage out of parent range
        assertFailsWith<IllegalArgumentException> {
            SleepSessionRecord(
                startTime = startTime,
                endTime = endTime,
                stages = listOf(
                    SleepSessionRecord.Stage(
                        startTime = Instant.fromEpochMilliseconds(500),
                        endTime = Instant.fromEpochMilliseconds(1500),
                        type = SleepStageType.Light
                    )
                ),
                metadata = metadata
            )
        }
    }

    @Test
    fun exerciseSessionRecord_validation() {
        val record = ExerciseSessionRecord(
            startTime = startTime,
            endTime = endTime,
            exerciseType = ExerciseType.Running,
            exerciseRoute = null,
            metadata = metadata
        )
        assertEquals(ExerciseType.Running, record.exerciseType)

        assertFailsWith<IllegalArgumentException> {
            ExerciseSessionRecord(
                startTime = endTime,
                endTime = startTime,
                exerciseType = ExerciseType.Running,
                exerciseRoute = null,
                metadata = metadata
            )
        }
    }
}
