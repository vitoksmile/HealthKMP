package com.vitoksmile.kmp.health

actual class HealthManagerFactory {

    actual fun createManager(): HealthManager =
        HealthConnectManager(ApplicationContextHolder.applicationContext)
}