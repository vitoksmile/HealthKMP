package com.viktormykhailiv.kmp.health

import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.LeanBodyMassRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import com.viktormykhailiv.kmp.health.HealthDataType.BloodGlucose
import com.viktormykhailiv.kmp.health.HealthDataType.BloodPressure
import com.viktormykhailiv.kmp.health.HealthDataType.BodyFat
import com.viktormykhailiv.kmp.health.HealthDataType.BodyTemperature
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Height
import com.viktormykhailiv.kmp.health.HealthDataType.LeanBodyMass
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import kotlin.reflect.KClass

internal fun HealthDataType.toRecordType(): KClass<out Record> = when (this) {
    BloodGlucose -> BloodGlucoseRecord::class

    BloodPressure -> BloodPressureRecord::class

    BodyFat -> BodyFatRecord::class

    BodyTemperature -> BodyTemperatureRecord::class

    HeartRate -> HeartRateRecord::class

    Height -> HeightRecord::class

    LeanBodyMass -> LeanBodyMassRecord::class

    Sleep -> SleepSessionRecord::class

    Steps -> StepsRecord::class

    Weight -> WeightRecord::class
}

/**
 * Returns a permission defined in [HealthPermission] to read provided [HealthDataType].
 */
internal fun HealthDataType.toHealthPermission(
    isRead: Boolean = false,
    isWrite: Boolean = false,
): String {
    require(isRead != isWrite)

    return if (isRead) {
        HealthPermission.getReadPermission(recordType = toRecordType())
    } else {
        HealthPermission.getWritePermission(recordType = toRecordType())
    }
}