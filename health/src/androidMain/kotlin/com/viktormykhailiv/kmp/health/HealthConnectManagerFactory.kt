@file:Suppress("DEPRECATION", "unused")

package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.legacy.GoogleFitManager

actual class HealthManagerFactory {

    actual fun createManager(): HealthManager {
        val healthConnectManager = HealthConnectManager(ApplicationContextHolder.applicationContext)

        return if (healthConnectManager.isAvailable().getOrNull() == true) {
            healthConnectManager
        } else {
            GoogleFitManager(ApplicationContextHolder.applicationContext)
        }
    }

    actual fun createManager(options: Options): HealthManager {
        val healthConnectManager = HealthConnectManager(ApplicationContextHolder.applicationContext)

        return if (healthConnectManager.isAvailable().getOrNull() == true) {
            healthConnectManager
        } else if (options.useGoogleFit) {
            GoogleFitManager(ApplicationContextHolder.applicationContext)
        } else {
            NoHealthManager()
        }
    }

    actual data class Options(
        val useGoogleFit: Boolean
    )
}