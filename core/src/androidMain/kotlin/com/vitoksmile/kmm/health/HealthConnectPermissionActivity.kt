package com.vitoksmile.kmm.health

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.PermissionController
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Health Connect permissions dialog not shown on subsequent requests
 *
 * https://issuetracker.google.com/issues/233239418
 */
internal class HealthConnectPermissionActivity : AppCompatActivity() {

    companion object {
        private const val KEY_READ_PERMISSIONS = "KEY_READ_PERMISSIONS"
        private const val KEY_WRITE_PERMISSIONS = "KEY_WRITE_PERMISSIONS"

        private var continuation: CancellableContinuation<Result<Boolean>>? = null

        suspend fun request(
            context: Context,
            readPermissions: Set<String>,
            writePermissions: Set<String>,
        ): Result<Boolean> = suspendCancellableCoroutine {
            continuation?.cancel()
            continuation = it

            context.startActivity(
                Intent(context, HealthConnectPermissionActivity::class.java)
                    .putExtra(KEY_READ_PERMISSIONS, readPermissions.toTypedArray())
                    .putExtra(KEY_WRITE_PERMISSIONS, writePermissions.toTypedArray())
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
    }

    private val permissions: Set<String> by lazy {
        val readPermissions = intent.getStringArrayExtra(KEY_READ_PERMISSIONS).orEmpty().toSet()
        val writePermissions = intent.getStringArrayExtra(KEY_WRITE_PERMISSIONS).orEmpty().toSet()
        readPermissions + writePermissions
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

        val readPermissions = intent.getStringArrayExtra(KEY_READ_PERMISSIONS).orEmpty().toSet()
        val writePermissions = intent.getStringArrayExtra(KEY_WRITE_PERMISSIONS).orEmpty().toSet()
        val permissions = readPermissions + writePermissions
        requestPermissions.launch(permissions)
    }

    override fun onDestroy() {
        super.onDestroy()
        continuation?.cancel()
        continuation = null
    }
}