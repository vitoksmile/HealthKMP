package com.viktormykhailiv.kmp.health.legacy

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.HistoryClient
import com.google.android.gms.fitness.request.DataReadRequest
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthManager
import com.viktormykhailiv.kmp.health.HealthRecord
import kotlinx.datetime.Instant
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
@Deprecated("The Google Fit APIs will no longer be available after June 30, 2025. As of May 1, 2024, developers cannot sign up to use these APIs.")
class GoogleFitManager(
    private val context: Context,
) : HealthManager {

    override fun isAvailable(): Result<Boolean> = runCatching {
        val fitPackage = "com.google.android.apps.fitness"
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    fitPackage,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong())
                )
            } else {
                context.packageManager.getPackageInfo(
                    fitPackage,
                    PackageManager.GET_ACTIVITIES,
                )
            }
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    override suspend fun isAuthorized(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Result<Boolean> =
        GoogleFitPermissionActivity.hasPermission(
            context,
            fitnessOptions(readTypes = readTypes, writeTypes = writeTypes),
        )

    override suspend fun requestAuthorization(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Result<Boolean> =
        isAuthorized(readTypes = readTypes, writeTypes = writeTypes)
            .mapCatching { isAuthorized ->
                if (isAuthorized) return@mapCatching true

                try {
                    GoogleFitPermissionActivity.request(
                        context,
                        fitnessOptions(readTypes = readTypes, writeTypes = writeTypes),
                    ).getOrThrow()

                    isAuthorized(readTypes = readTypes, writeTypes = writeTypes)
                        .getOrThrow()
                } catch (ignored: CancellationException) {
                    false
                } catch (ex: Throwable) {
                    throw ex
                }
            }

    override suspend fun isRevokeAuthorizationSupported(): Result<Boolean> =
        Result.success(true)

    override suspend fun revokeAuthorization(): Result<Unit> = runCatching {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(context, options).signOut().await()
    }

    override suspend fun readData(
        startTime: Instant,
        endTime: Instant,
        type: HealthDataType,
    ): Result<List<HealthRecord>> = runCatching {
        val response = getHistoryClient()
            .getOrThrow()
            .readData(
                DataReadRequest.Builder()
                    .read(type.toDataType())
                    .setTimeRange(
                        startTime.toEpochMilliseconds(),
                        endTime.toEpochMilliseconds(),
                        TimeUnit.MILLISECONDS,
                    )
                    .build()
            )
            .await()

        response.dataSets.first()
            .dataPoints
            .toHealthRecords(type)
    }

    /**
     * Inserts one or more [HealthRecord]. Insertion of
     * multiple [records] is executed one by one - if one fails, other might be inserted.
     */
    override suspend fun writeData(
        records: List<HealthRecord>,
    ): Result<Unit> = runCatching {
        val client = getHistoryClient().getOrThrow()

        records.toDataSets(context)
            .forEach { dataSet ->
                client
                    .insertData(dataSet)
                    .await()
            }
    }

    @Suppress("DEPRECATION")
    private fun getHistoryClient(): Result<HistoryClient> = runCatching {
        val account = GoogleSignIn.getLastSignedInAccount(context)
            ?: throw SecurityException("Not authorized")

        Fitness.getHistoryClient(context, account)
    }
}