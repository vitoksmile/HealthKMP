@file:Suppress("NestedLambdaShadowedImplicitParameter", "DEPRECATION")

package com.viktormykhailiv.kmp.health.legacy

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.FitnessOptions
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Deprecated("The Google Fit APIs will be deprecated in 2026")
internal class GoogleFitPermissionActivity : ComponentActivity() {

    companion object {
        private const val KEY_SCOPES = "KEY_SCOPES"
        private const val REQUEST_CODE = 1

        private var continuation: CancellableContinuation<Result<Boolean>>? = null

        fun hasPermission(
            context: Context,
            options: FitnessOptions,
        ): Result<Boolean> = runCatching {
            val hasAndroidPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION,
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

            val account = GoogleSignIn.getLastSignedInAccount(context)
            val hasGooglePermission = GoogleSignIn.hasPermissions(
                account,
                options,
            )

            hasAndroidPermission && hasGooglePermission
        }

        suspend fun request(
            context: Context,
            options: FitnessOptions,
        ): Result<Boolean> = suspendCancellableCoroutine {
            continuation?.cancel()
            continuation = it

            context.startActivity(
                Intent(context, GoogleFitPermissionActivity::class.java)
                    .putStringArrayListExtra(
                        KEY_SCOPES,
                        ArrayList(options.impliedScopes.map { it.scopeUri }),
                    )
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
    }

    private val scopes: List<Scope>
        get() = intent.getStringArrayListExtra(KEY_SCOPES)
            .orEmpty()
            .map(::Scope)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val options = object : GoogleSignInOptionsExtension {
            override fun getExtensionType(): Int = 3
            override fun toBundle(): Bundle = Bundle()
            override fun getImpliedScopes(): MutableList<Scope> = scopes.toMutableList()
        }
        GoogleSignIn.requestPermissions(
            this,
            REQUEST_CODE,
            GoogleSignIn.getLastSignedInAccount(this),
            options,
        )
    }

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_CODE) return

        if (resultCode != RESULT_OK) {
            resume(hasPermission = false)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val hasPermission = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION,
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                resume(hasPermission = true)
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), REQUEST_CODE)
            }
        } else {
            resume(hasPermission = true)
        }
    }

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_CODE) return

        resume(hasPermission = grantResults.all { it == PackageManager.PERMISSION_GRANTED })
    }

    private fun resume(hasPermission: Boolean) {
        continuation?.resume(Result.success(hasPermission))
        continuation = null
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        continuation?.cancel()
        continuation = null
    }
}