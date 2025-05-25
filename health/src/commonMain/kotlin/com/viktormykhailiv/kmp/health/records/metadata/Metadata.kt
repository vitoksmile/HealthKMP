package com.viktormykhailiv.kmp.health.records.metadata

import com.viktormykhailiv.kmp.health.HealthRecord

/**
 * Set of shared metadata fields for [HealthRecord].
 *
 * @param recordingMethod Client supplied data recording method to help to understand how the data was recorded.
 * @param id Unique identifier of this data.
 * @param device Optional client supplied device information associated with the data.
 */
data class Metadata internal constructor(
    val recordingMethod: RecordingMethod,
    val id: String,
    val device: Device?,
) {

    sealed interface RecordingMethod {

        /**
         * This should only be used in the case when the recording method is unknown.
         */
        data object Unknown : RecordingMethod

        /**
         * Manually entered record.
         */
        data object ManualEntry : RecordingMethod

        /**
         * Automatically recorded record.
         */
        data object AutoRecorded : RecordingMethod
    }

    companion object {

        internal const val EMPTY_ID: String = ""

        /**
         * Creates Metadata with unknown recording method.
         *
         * [RecordingMethod.Unknown] is auto populated.
         *
         * This should only be used in the case when the recording method is unknown.
         *
         * @param id The existing UUID of the record.
         * @param device The optional [Device] associated with the record.
         */
        fun unknownRecordingMethod(id: String = EMPTY_ID, device: Device? = null): Metadata =
            Metadata(
                recordingMethod = RecordingMethod.Unknown,
                id = id,
                device = device,
            )

        /**
         * Creates Metadata for a manually entered record.
         *
         * [RecordingMethod.ManualEntry] is auto populated.
         *
         * @param id The existing UUID of the record.
         * @param device The optional [Device] associated with the record.
         */
        fun manualEntry(id: String = EMPTY_ID, device: Device? = null): Metadata =
            Metadata(
                recordingMethod = RecordingMethod.ManualEntry,
                id = id,
                device = device,
            )

        /**
         * Creates Metadata for an automatically recorded record.
         *
         * [RecordingMethod.AutoRecorded] is auto populated.
         *
         * @param id The existing UUID of the record.
         * @param device The [Device] associated with the record.
         */
        fun autoRecorded(id: String = EMPTY_ID, device: Device): Metadata =
            Metadata(
                recordingMethod = RecordingMethod.AutoRecorded,
                id = id,
                device = device,
            )
    }
}
