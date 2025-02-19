package com.viktormykhailiv.kmp.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.CancellationException
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant

class HealthConnectManager(
    private val context: Context,
) : HealthManager {

    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    override fun isAvailable(): Result<Boolean> = runCatching {
        DistanceRecord.DISTANCE_TOTAL
        HeartRateRecord.BPM_AVG
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
                } catch (ignored: CancellationException) {
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

        response.records.mapNotNull { it.toHealthRecord() }
    }

    /**
     * Inserts one or more [HealthRecord]. Insertion of
     * multiple [records] is executed in a transaction - if one fails, none is inserted.
     */
    override suspend fun writeData(
        records: List<HealthRecord>,
    ): Result<Unit> = runCatching {
        healthConnectClient.insertRecords(records.mapNotNull { it.toHCRecord() })
    }

    override suspend fun aggregate(
        startTime: Instant,
        endTime: Instant,
        type: HealthDataType,
    ): Result<HealthAggregatedRecord> = runCatching {
        val request = AggregateRequest(
            metrics = type.toAggregateMetrics(),
            timeRangeFilter = TimeRangeFilter.between(
                startTime = startTime.toJavaInstant(),
                endTime = endTime.toJavaInstant()
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

private val List<HealthDataType>.readPermissions: Set<String>
    get() = map { it.toHealthPermission(isRead = true) }.toSet()

private val List<HealthDataType>.writePermissions: Set<String>
    get() = map { it.toHealthPermission(isWrite = true) }.toSet()