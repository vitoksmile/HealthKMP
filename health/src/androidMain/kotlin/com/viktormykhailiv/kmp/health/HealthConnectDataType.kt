package com.viktormykhailiv.kmp.health

import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.LeanBodyMassRecord
import androidx.health.connect.client.records.PowerRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import com.viktormykhailiv.kmp.health.HealthDataType.BloodGlucose
import com.viktormykhailiv.kmp.health.HealthDataType.BloodPressure
import com.viktormykhailiv.kmp.health.HealthDataType.BodyFat
import com.viktormykhailiv.kmp.health.HealthDataType.BodyTemperature
import com.viktormykhailiv.kmp.health.HealthDataType.Exercise
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Height
import com.viktormykhailiv.kmp.health.HealthDataType.LeanBodyMass
import com.viktormykhailiv.kmp.health.HealthDataType.CyclingPedalingCadence
import com.viktormykhailiv.kmp.health.HealthDataType.Power
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import kotlin.reflect.KClass

internal fun HealthDataType.toRecordType(): KClass<out Record> = when (this) {
    BloodGlucose -> BloodGlucoseRecord::class

    BloodPressure -> BloodPressureRecord::class

    BodyFat -> BodyFatRecord::class

    BodyTemperature -> BodyTemperatureRecord::class

    CyclingPedalingCadence -> CyclingPedalingCadenceRecord::class

    is Exercise -> ExerciseSessionRecord::class

    HeartRate -> HeartRateRecord::class

    Height -> HeightRecord::class

    LeanBodyMass -> LeanBodyMassRecord::class

    Power -> PowerRecord::class

    Sleep -> SleepSessionRecord::class

    Steps -> StepsRecord::class

    Weight -> WeightRecord::class
}

/**
 * Returns permissions defined in [HealthPermission] to access [HealthDataType].
 */
internal fun HealthDataType.toHealthPermissions(
    isRead: Boolean = false,
    isWrite: Boolean = false,
): Set<String> {
    require(isRead != isWrite)

    val permissions = mutableSetOf(
        if (isRead) {
            HealthPermission.getReadPermission(toRecordType())
        } else {
            HealthPermission.getWritePermission(recordType = toRecordType())
        }
    )

    if (this is Exercise) {
        permissions.add(HealthPermission.PERMISSION_WRITE_EXERCISE_ROUTE)
    }

    return permissions
}