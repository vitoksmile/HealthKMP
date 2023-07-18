@file:Suppress("unused", "FunctionName")

import androidx.compose.ui.window.ComposeUIViewController
import com.vitoksmile.kmm.health.commonModule
import org.koin.core.context.startKoin

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = ComposeUIViewController { App() }

fun InitKoin() {
    startKoin {
        modules(commonModule())
    }
}