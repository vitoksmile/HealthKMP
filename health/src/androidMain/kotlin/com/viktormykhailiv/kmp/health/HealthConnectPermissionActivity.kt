package com.viktormykhailiv.kmp.health

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.health.connect.client.PermissionController
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Health Connect permissions dialog not shown on subsequent requests
 *
 * https://issuetracker.google.com/issues/233239418
 */
internal class HealthConnectPermissionActivity : ComponentActivity() {

    companion object {
        private const val KEY_READ_PERMISSIONS = "KEY_READ_PERMISSIONS"
        private const val KEY_WRITE_PERMISSIONS = "KEY_WRITE_PERMISSIONS"
        private const val KEY_OTHER_PERMISSIONS = "KEY_OTHER_PERMISSIONS"

        private var continuation: CancellableContinuation<Result<Boolean>>? = null

        /**
         * Requests the specified Health Connect permissions by launching the [HealthConnectPermissionActivity].
         *
         * This is a suspending function that waits for the user to interact with the permission dialog
         * and returns a [Result] indicating whether all requested permissions were granted.
         *
         * @param context The context used to start the activity.
         * @param readPermissions A set of Health Connect read permission strings to request.
         * @param writePermissions A set of Health Connect write permission strings to request.
         * @param otherPermission A set of additional permission strings to request.
         * @return A [Result] wrapping a [Boolean]. Returns `true` if all requested permissions
         * were granted, `false` otherwise.
         */
        suspend fun request(
            context: Context,
            readPermissions: Set<String>,
            writePermissions: Set<String>,
            otherPermission: Set<String>,
        ): Result<Boolean> = suspendCancellableCoroutine {
            continuation?.cancel()
            continuation = it

            context.startActivity(
                Intent(context, HealthConnectPermissionActivity::class.java)
                    .putExtra(KEY_READ_PERMISSIONS, readPermissions.toTypedArray())
                    .putExtra(KEY_WRITE_PERMISSIONS, writePermissions.toTypedArray())
                    .putExtra(KEY_OTHER_PERMISSIONS, otherPermission.toTypedArray())
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
    }

    private val permissions: Set<String>
        get() {
            val readPermissions =
                intent.getStringArrayExtra(KEY_READ_PERMISSIONS).orEmpty().toSet()
            val writePermissions =
                intent.getStringArrayExtra(KEY_WRITE_PERMISSIONS).orEmpty().toSet()
            val otherPermission =
                intent.getStringArrayExtra(KEY_OTHER_PERMISSIONS).orEmpty().toSet()
            return readPermissions + writePermissions + otherPermission
        }

    private val contract = PermissionController.createRequestPermissionResultContract()
    private val requestPermissions = registerForActivityResult(contract) { grantedPermissions ->
        val granted = grantedPermissions.containsAll(permissions)
        continuation?.resume(Result.success(granted))
        continuation = null
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions.launch(permissions)
    }

    override fun onDestroy() {
        super.onDestroy()
        continuation?.cancel()
        continuation = null
    }
}