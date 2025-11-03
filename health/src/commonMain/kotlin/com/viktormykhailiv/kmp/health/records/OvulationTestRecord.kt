package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.OvulationTest
import com.viktormykhailiv.kmp.health.InstantaneousRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import kotlin.time.Instant

/**
 * Each record represents the result of an ovulation test.
 *
 * @param result The result of a user's ovulation test, which shows if they're ovulating or not.
 */
data class OvulationTestRecord(
    override val time: Instant,
    val result: Result?,
    override val metadata: Metadata,
) : InstantaneousRecord {

    override val dataType: HealthDataType = OvulationTest

    /**
     * The result of a user's ovulation test.
     */
    sealed interface Result {

        /**
         * Inconclusive result. Refers to ovulation test results that are indeterminate (e.g. may be
         * testing malfunction, user error, etc.). ". Any unknown value will also be returned as
         * `Inconclusive`.
         */
        data object Inconclusive : Result

        /**
         * Positive fertility (may also be referred as "peak" fertility). Refers to the peak of the
         * luteinizing hormone (LH) surge and ovulation is expected to occur in 10-36 hours.
         */
        data object Positive : Result

        /**
         * High fertility. Refers to a rise in estrogen or luteinizing hormone that may signal the
         * fertile window (time in the menstrual cycle when conception is likely to occur).
         */
        data object High : Result

        /**
         * Negative fertility (may also be referred as "low" fertility). Refers to the time in the
         * cycle where fertility/conception is expected to be low.
         */
        data object Negative : Result
    }

}
