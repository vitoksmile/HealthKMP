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
            .mapCatching { isAuthorized ->
                if (isAuthorized) return@mapCatching true

                val otherPermission = if (requestReadHealthDataInBackground) {
                    setOf(
                        getReadHealthDataInBackgroundKey()
                    )
                } else {
                    emptySet()
                }
                try {
                    HealthConnectPermissionActivity.request(
                        context,
                        readPermissions = readTypes.readPermissions,
                        writePermissions = writeTypes.writePermissions,
                        otherPermission = otherPermission
                    ).getOrThrow()
                } catch (_: CancellationException) {
                    false
                } catch (ex: Throwable) {
                    throw ex
                }
            }

    override suspend fun isRevokeAuthorizationSupported(): Result<Boolean> = Result.success(true)

    override suspend fun revokeAuthorization(): Result<Unit> = runCatching {
        healthConnectClient.permissionController.revokeAllPermissions()
    }

    override suspend fun hasReadHealthDataInBackgroundPermission(): Result<Boolean> = runCatching {
        val grantedPermissions = healthConnectClient.permissionController.getGrantedPermissions()
        val key = getReadHealthDataInBackgroundKey()
        grantedPermissions.contains(key)
    }

    override suspend fun requestReadHealthDataInBackground(): Result<Boolean> {
        return hasReadHealthDataInBackgroundPermission()
            .mapCatching { hasBackgroundPermission ->
                if (hasBackgroundPermission) return@mapCatching true
                try {
                    val key = getReadHealthDataInBackgroundKey()
                    HealthConnectPermissionActivity.request(
                        context,
                        readPermissions = emptySet(),
                        writePermissions = emptySet(),
                        otherPermission = setOf(key)
                    ).getOrThrow()
                } catch (_: CancellationException) {
                    false
                } catch (ex: Throwable) {
                    throw ex
                }
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
    get() = map { it.toHealthPermissions(isRead = true) }.flatten().toSet()

private val List<HealthDataType>.writePermissions: Set<String>
    get() = map { it.toHealthPermissions(isWrite = true) }.flatten().toSet()
