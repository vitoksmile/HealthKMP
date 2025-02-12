package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.legacy.GoogleFitManager

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
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