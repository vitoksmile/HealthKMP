package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.Height
import com.viktormykhailiv.kmp.health.InstantaneousRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.requireNotLess
import com.viktormykhailiv.kmp.health.requireNotMore
import com.viktormykhailiv.kmp.health.units.Length
import com.viktormykhailiv.kmp.health.units.meters
import kotlin.time.Instant

/**
 * Captures the user's height.
 */
data class HeightRecord(
    override val time: Instant,
    val height: Length,
    override val metadata: Metadata,
) : InstantaneousRecord {

    override val dataType: HealthDataType = Height

    init {
        height.requireNotLess(other = height.zero(), name = "height")
        height.requireNotMore(other = MAX_HEIGHT, name = "height")
    }

    private companion object {
        val MAX_HEIGHT = 3.meters
    }

}
