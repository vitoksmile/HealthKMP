package com.viktormykhailiv.kmp.health

actual class HealthManagerFactory {

    actual fun createManager(): HealthManager =
        HealthKitManager()
}