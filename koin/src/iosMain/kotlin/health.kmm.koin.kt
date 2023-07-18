@file:Suppress("unused")

import com.vitoksmile.kmm.health.koin.commonModule
import org.koin.core.context.startKoin

fun start() {
    startKoin {
        modules(commonModule())
    }
}