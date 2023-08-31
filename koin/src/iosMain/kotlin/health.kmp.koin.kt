@file:Suppress("unused")

import com.vitoksmile.kmp.health.koin.commonModule
import org.koin.core.context.startKoin

fun start() {
    startKoin {
        modules(commonModule())
    }
}