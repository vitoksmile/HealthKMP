@file:Suppress("ClassName")

package com.vitoksmile.kmm.health

class iOSHealthManager : HealthManager {

    override fun isAvailable(): Result<Boolean> = runCatching {
        throw NotImplementedError("HealthKit is not implemented yet")
    }
}

actual class HealthManagerFactory {
    actual fun createManager(): HealthManager = iOSHealthManager()
}
