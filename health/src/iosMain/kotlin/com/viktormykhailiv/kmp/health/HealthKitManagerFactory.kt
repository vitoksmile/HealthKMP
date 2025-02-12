package com.viktormykhailiv.kmp.health

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class HealthManagerFactory {

    actual fun createManager(): HealthManager =
        HealthKitManager()
}