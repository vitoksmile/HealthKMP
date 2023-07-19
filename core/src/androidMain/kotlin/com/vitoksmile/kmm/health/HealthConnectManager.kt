package com.vitoksmile.kmm.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import kotlinx.coroutines.CancellationException

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
        writeTypes: List<HealthDataType>
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
}

private val List<HealthDataType>.readPermissions: Set<String>
    get() = map { it.toHealthPermission(isRead = true) }.toSet()

private val List<HealthDataType>.writePermissions: Set<String>
    get() = map { it.toHealthPermission(isWrite = true) }.toSet()