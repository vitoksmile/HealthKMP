package com.viktormykhailiv.kmp.health

/**
 * Common interface shared by aggregated records.
 */
interface HealthAggregatedRecord {

    /**
     * The [HealthDataType] of the aggregated data.
     */
    val dataType: HealthDataType
}
