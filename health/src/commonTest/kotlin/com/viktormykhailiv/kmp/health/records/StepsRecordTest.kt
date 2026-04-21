package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class StepsRecordTest {

    private val startTime = Instant.fromEpochMilliseconds(1000)
    private val endTime = Instant.fromEpochMilliseconds(2000)
    private val metadata = Metadata.unknownRecordingMethod()

    @Test
    fun validStepsRecord_shouldBeCreated() {
        val steps = StepsRecord(
            startTime = startTime,
            endTime = endTime,
            count = 100,
            metadata = metadata
        )

        assertEquals(startTime, steps.startTime)
        assertEquals(endTime, steps.endTime)
        assertEquals(100, steps.count)
    }

    @Test
    fun stepsRecordWithZeroCount_shouldFail() {
        assertFailsWith<IllegalArgumentException> {
            StepsRecord(
                startTime = startTime,
                endTime = endTime,
                count = 0,
                metadata = metadata
            )
        }
    }

    @Test
    fun stepsRecordWithInvalidTimeRange_shouldFail() {
        assertFailsWith<IllegalArgumentException> {
            StepsRecord(
                startTime = endTime,
                endTime = startTime,
                count = 100,
                metadata = metadata
            )
        }
    }
}
