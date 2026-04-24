package com.viktormykhailiv.kmp.health

import android.content.Context
import android.health.connect.HealthPermissions
import android.os.Build
import android.os.ext.SdkExtensions
import androidx.core.text.util.LocalePreferences
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.viktormykhailiv.kmp.health.HealthDataType.BloodGlucose
import com.viktormykhailiv.kmp.health.HealthDataType.BodyFat
import com.viktormykhailiv.kmp.health.HealthDataType.BodyTemperature
import com.viktormykhailiv.kmp.health.HealthDataType.LeanBodyMass
import com.viktormykhailiv.kmp.health.region.RegionalPreferences
import com.viktormykhailiv.kmp.health.region.TemperatureRegionalPreference
import kotlinx.coroutines.CancellationException
import kotlin.time.Instant
import kotlin.time.toJavaInstant

/**
 * Android implementation of [HealthManager] using Health Connect.
 *
 * @param context The application context.
 */
class HealthConnectManager(
    private val context: Context,
) : HealthManager {

    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    override fun isAvailable(): Result<Boolean> = runCatching {
        val status = HealthConnectClient.getSdkStatus(context)
        status == HealthConnectClient.SDK_AVAILABLE
    }

    override suspend fun isAuthorized(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Result<Boolean> = runCatching {
        val grantedPermissions = healthConnectClient.permissionController.getGrantedPermissions()

        grantedPermissions.containsAll(readTypes.readPermissions) &&
                grantedPermissions.containsAll(writeTypes.writePermissions)
    }

    override suspend fun requestAuthorization(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
        requestReadHealthDataInBackground: Boolean
    ): Result<Boolean> =
        isAuthorized(readTypes = readTypes, writeTypes = writeTypes)
            .flatMap { isAuthorized ->
                if (isAuthorized && requestReadHealthDataInBackground) {
                    hasReadHealthDataInBackgroundPermission()
                } else {
                    Result.success(isAuthorized)
                }
            }
            .flatMap { isAuthorized ->
                if (isAuthorized) return@flatMap Result.success(true)

                requestPermissionWithActivity(
                    readPermissions = readTypes.readPermissions,
                    writePermissions = writeTypes.writePermissions,
                    otherPermission = if (requestReadHealthDataInBackground) {
                        setOf(getReadHealthDataInBackgroundKey())
                    } else {
                        emptySet()
                    },
                )
            }

    override suspend fun isRevokeAuthorizationSupported(): Result<Boolean> =
        Result.success(true)

    override suspend fun revokeAuthorization(): Result<Unit> = runCatching {
        healthConnectClient.permissionController.revokeAllPermissions()
    }

    override suspend fun hasReadHealthDataInBackgroundPermission(): Result<Boolean> = runCatching {
        val grantedPermissions = healthConnectClient.permissionController.getGrantedPermissions()
        val key = getReadHealthDataInBackgroundKey()
        key in grantedPermissions
    }

    override suspend fun requestReadHealthDataInBackgroundPermission(): Result<Boolean> {
        return hasReadHealthDataInBackgroundPermission()
            .flatMap { hasBackgroundPermission ->
                if (hasBackgroundPermission) return Result.success(true)

                requestPermissionWithActivity(
                    readPermissions = emptySet(),
                    writePermissions = emptySet(),
                    otherPermission = setOf(getReadHealthDataInBackgroundKey()),
                )
            }
    }

    override suspend fun readData(
        startTime: Instant,
        endTime: Instant,
        type: HealthDataType,
    ): Result<List<HealthRecord>> = runCatching {
        val request = ReadRecordsRequest(
            recordType = type.toRecordType(),
            timeRangeFilter = TimeRangeFilter.between(
                startTime = startTime.toJavaInstant(),
                endTime = endTime.toJavaInstant()
            ),
            ascendingOrder = false,
        )
        val response = healthConnectClient.readRecords(request)

        val temperaturePreference = { getTemperaturePreference() }

        response.records.mapNotNull { it.toHealthRecord(temperaturePreference) }
    }

    /**
     * Inserts one or more [HealthRecord]. Insertion of
     * multiple [records] is executed in a transaction - if one fails, none is inserted.
     */
    override suspend fun writeData(
        records: List<HealthRecord>,
    ): Result<Unit> = runCatching {
        val temperaturePreference = { getTemperaturePreference() }

        healthConnectClient.insertRecords(records.mapNotNull { it.toHCRecord(temperaturePreference) })
    }

    override suspend fun aggregate(
        startTime: Instant,
        endTime: Instant,
        type: HealthDataType,
    ): Result<HealthAggregatedRecord> = runCatching {
        when (type) {
            BloodGlucose -> {
                aggregateBloodGlucose(startTime = startTime, endTime = endTime)
            }

            BodyFat -> {
                aggregateBodyFat(startTime = startTime, endTime = endTime)
            }

            BodyTemperature -> {
                aggregateBodyTemperature(startTime = startTime, endTime = endTime)
            }

            LeanBodyMass -> {
                aggregateLeanBodyMass(startTime = startTime, endTime = endTime)
            }

            else -> {
                val request = AggregateRequest(
                    metrics = type.toAggregateMetrics(),
                    timeRangeFilter = TimeRangeFilter.between(
                        startTime = startTime.toJavaInstant(),
                        endTime = endTime.toJavaInstant(),
                    ),
                )
                val response = healthConnectClient.aggregate(request)

                response.toHealthAggregatedRecord(
                    startTime = startTime,
                    endTime = endTime,
                    type = type,
                )
            }
        }
    }

    override suspend fun getRegionalPreferences(): Result<RegionalPreferences> = runCatching {
        RegionalPreferences(
            temperature = getTemperaturePreference(),
        )
    }

    private fun getTemperaturePreference(): TemperatureRegionalPreference {
        return when (LocalePreferences.getTemperatureUnit()) {
            LocalePreferences.TemperatureUnit.FAHRENHEIT -> TemperatureRegionalPreference.Fahrenheit
            else -> TemperatureRegionalPreference.Celsius
        }
    }

    private suspend fun requestPermissionWithActivity(
        readPermissions: Set<String>,
        writePermissions: Set<String>,
        otherPermission: Set<String>,
    ): Result<Boolean> {
        return HealthConnectPermissionActivity.request(
            context = context,
            readPermissions = readPermissions,
            writePermissions = writePermissions,
            otherPermission = otherPermission,
        ).recoverCatching { error ->
            if (error is CancellationException) {
                false
            } else {
                throw error
            }
        }
    }

    private fun getReadHealthDataInBackgroundKey(): String {
        return if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            SdkExtensions.getExtensionVersion(Build.VERSION_CODES.UPSIDE_DOWN_CAKE) >= 13
        ) {
            HealthPermissions.READ_HEALTH_DATA_IN_BACKGROUND
        } else {
            "android.permission.health.READ_HEALTH_DATA_IN_BACKGROUND"
        }
    }
}

private val List<HealthDataType>.readPermissions: Set<String>
    get() = flatMap { it.toHealthPermissions(isRead = true) }.toSet()

private val List<HealthDataType>.writePermissions: Set<String>
    get() = flatMap { it.toHealthPermissions(isWrite = true) }.toSet()
