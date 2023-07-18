package com.vitoksmile.kmm.health

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.compose.LocalKoinScope

interface HealthManager {

    fun isAvailable(): Result<Boolean>
}

expect class HealthManagerFactory {
    fun createManager(): HealthManager
}

@Composable
internal fun rememberHealthManager(): HealthManager {
    val scope = LocalKoinScope.current
    return remember(scope) {
        scope.get<HealthManagerFactory>().createManager()
    }
}