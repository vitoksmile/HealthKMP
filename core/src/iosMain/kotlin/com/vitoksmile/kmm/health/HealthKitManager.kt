@file:Suppress("ClassName")

package com.vitoksmile.kmm.health

class HealthKitManager : HealthManager {

    override fun isAvailable(): Result<Boolean> = runCatching {
        throw NotImplementedError("HealthKit is not implemented yet")
    }
}