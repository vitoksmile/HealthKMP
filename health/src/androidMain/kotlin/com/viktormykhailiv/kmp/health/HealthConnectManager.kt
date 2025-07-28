package com.viktormykhailiv.kmp.health

import android.content.Context
import androidx.core.text.util.LocalePreferences
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.viktormykhailiv.kmp.health.HealthDataType.BloodGlucose
import com.viktormykhailiv.kmp.health.HealthDataType.BodyTemperature
import com.viktormykhailiv.kmp.health.region.RegionalPreferences
import com.viktormykhailiv.kmp.health.region.TemperatureRegionalPreference
import kotlinx.coroutines.CancellationException
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant

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
    ): Result<Boolean> =
        isAuthorized(readTypes = readTypes, writeTypes = writeTypes)
            .mapCatching { isAuthorized ->
                if (isAuthorized) return@mapCatching true

                try {
                    HealthConnectPermissionActivity.request(
                        context,
                        readPermissions = readTypes.readPermissions,
                        writePermissions = writeTypes.writePermissions,
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

            BodyTemperature -> {
                aggregateBodyTemperature(startTime = startTime, endTime = endTime)
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

}

private val List<HealthDataType>.readPermissions: Set<String>
    get() = map { it.toHealthPermission(isRead = true) }.toSet()

private val List<HealthDataType>.writePermissions: Set<String>
    get() = map { it.toHealthPermission(isWrite = true) }.toSet()