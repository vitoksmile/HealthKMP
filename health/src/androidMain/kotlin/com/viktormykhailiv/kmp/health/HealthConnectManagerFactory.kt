@file:Suppress("DEPRECATION")

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

}