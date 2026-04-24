@file:Suppress("DEPRECATION", "unused")

package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.legacy.GoogleFitManager
import com.viktormykhailiv.kmp.health.legacy.NoHealthManager

/**
 * Factory class responsible for creating [HealthManager] instances.
 *
 * This implementation determines the most appropriate [HealthManager] based on device capabilities
 * and the provided [HealthManagerFactoryOptions]. It prioritizes Health Connect, falling back
 * to Google Fit if enabled in options, or a no-op implementation if neither is available.
 */
actual class HealthManagerFactory {

    @Deprecated(
        "Use createManager with options",
        replaceWith = ReplaceWith(
            expression = "createManager(options = HealthManagerFactoryOptions.default())",
            imports = arrayOf("com.viktormykhailiv.kmp.health.HealthManagerFactoryOptions"),
        ),
    )
    fun createManager(): HealthManager =
        createManager(options = HealthManagerFactoryOptions.default())

    actual fun createManager(options: HealthManagerFactoryOptions): HealthManager {
        val healthConnectManager = HealthConnectManager(ApplicationContextHolder.applicationContext)

        return when {
            healthConnectManager.isAvailable().getOrNull() == true -> {
                healthConnectManager
            }

            options.useGoogleFit -> {
                GoogleFitManager(ApplicationContextHolder.applicationContext)
            }

            else -> {
                NoHealthManager()
            }
        }
    }

}

/**
 * Configuration options for creating a [HealthManager] instance.
 *
 * @property useGoogleFit Whether to fallback to Google Fit if Health Connect is unavailable.
 */
actual data class HealthManagerFactoryOptions(
    val useGoogleFit: Boolean,
) {

    actual companion object {
        actual fun default() = HealthManagerFactoryOptions(useGoogleFit = true)
    }
}
