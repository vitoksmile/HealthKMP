package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.SexualActivity
import com.viktormykhailiv.kmp.health.InstantaneousRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import kotlin.time.Instant

/**
 * Captures an occurrence of sexual activity. Each record is a single occurrence.
 *
 * @param protection Whether protection was used during sexual activity.
 */
data class SexualActivityRecord(
    override val time: Instant,
    val protection: Protection?,
    override val metadata: Metadata,
) : InstantaneousRecord {

    override val dataType: HealthDataType = SexualActivity

    /**
     * Whether protection was used during sexual activity.
     */
    sealed interface Protection {

        data object Protected : Protection

        data object Unprotected : Protection

        data object Unknown : Protection
    }

}
