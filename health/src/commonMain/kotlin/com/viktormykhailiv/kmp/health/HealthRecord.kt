package com.viktormykhailiv.kmp.health

import kotlinx.datetime.Instant

/**
 * Common interface shared by readable or writable records.
 */
interface HealthRecord {

    val dataType: HealthDataType
}

/**
 * A record that contains a series of measurements.
 */
interface SeriesRecord<out T : Any> : IntervalRecord {

    val samples: List<T>
}

/**
 * A record that contains a measurement with a time interval.
 *
 * @see InstantaneousRecord for records with instantaneous measurement.
 */
interface IntervalRecord : HealthRecord {

    /**
     * Start time of the record.
     */
    val startTime: Instant

    /**
     * End time of the record.
     */
    val endTime: Instant
}

/**
 * A record that contains an instantaneous measurement.
 *
 * @see IntervalRecord for records with measurement of a time interval.
 */
interface InstantaneousRecord : HealthRecord {

    /**
     * Time the record happened.
     */
    val time: Instant
}
