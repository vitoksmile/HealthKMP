@file:Suppress("unused")

package com.viktormykhailiv.kmp.health

actual class HealthManagerFactory {

    actual fun createManager(): HealthManager =
        HealthKitManager()

    actual fun createManager(options: Options): HealthManager =
        HealthKitManager()

    actual object Options
}