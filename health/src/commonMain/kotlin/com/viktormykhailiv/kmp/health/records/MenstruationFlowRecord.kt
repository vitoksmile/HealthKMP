package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.MenstruationFlow
import com.viktormykhailiv.kmp.health.InstantaneousRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import kotlin.time.Instant

/**
 * Captures a description of how heavy a user's menstrual flow was (light, medium, or heavy). Each
 * record represents a description of how heavy the user's menstrual bleeding was.
 *
 * @param flow How heavy the user's menstrual flow was.
 */
data class MenstruationFlowRecord(
    override val time: Instant,
    val flow: Flow,
    override val metadata: Metadata,
) : InstantaneousRecord {

    override val dataType: HealthDataType = MenstruationFlow

    /**
     * How heavy the user's menstruation flow was.
     */
    sealed interface Flow {

        data object Unknown : Flow

        data object Light : Flow

        data object Medium : Flow

        data object Heavy : Flow
    }

}
