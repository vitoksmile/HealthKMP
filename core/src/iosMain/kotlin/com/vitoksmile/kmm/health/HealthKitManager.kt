@file:Suppress("ClassName")

package com.vitoksmile.kmm.health

import platform.HealthKit.HKHealthStore

class HealthKitManager : HealthManager {

    override fun isAvailable(): Result<Boolean> = runCatching {
        HKHealthStore.isHealthDataAvailable()
    }
}