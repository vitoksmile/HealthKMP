package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

class OtherRecordsTest {

    private val time = Instant.fromEpochMilliseconds(1000)
    private val metadata = Metadata.unknownRecordingMethod()

    @Test
    fun menstruationFlowRecord_validation() {
        val record = MenstruationFlowRecord(
            time = time,
            flow = MenstruationFlowRecord.Flow.Medium,
            metadata = metadata
        )
        assertEquals(MenstruationFlowRecord.Flow.Medium, record.flow)
    }

    @Test
    fun menstruationPeriodRecord_validation() {
        val startTime = Instant.fromEpochMilliseconds(1000)
        val endTime = startTime + 5.days
        val record =
            MenstruationPeriodRecord(startTime = startTime, endTime = endTime, metadata = metadata)
        assertEquals(startTime, record.startTime)
        assertEquals(endTime, record.endTime)

        // Too long period
        assertFailsWith<IllegalArgumentException> {
            MenstruationPeriodRecord(
                startTime = startTime,
                endTime = startTime + 32.days,
                metadata = metadata
            )
        }

        // Invalid range
        assertFailsWith<IllegalArgumentException> {
            MenstruationPeriodRecord(startTime = endTime, endTime = startTime, metadata = metadata)
        }
    }

    @Test
    fun ovulationTestRecord_validation() {
        val record = OvulationTestRecord(
            time = time,
            result = OvulationTestRecord.Result.Positive,
            metadata = metadata
        )
        assertEquals(OvulationTestRecord.Result.Positive, record.result)
    }

    @Test
    fun sexualActivityRecord_validation() {
        val record = SexualActivityRecord(
            time = time,
            protection = SexualActivityRecord.Protection.Protected,
            metadata = metadata
        )
        assertEquals(SexualActivityRecord.Protection.Protected, record.protection)
    }
}
