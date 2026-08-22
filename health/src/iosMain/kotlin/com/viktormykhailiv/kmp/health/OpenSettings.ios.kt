package com.viktormykhailiv.kmp.health

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

@OptIn(ExperimentalForeignApi::class)
internal actual fun openAppleHealthSettings(): Result<Unit> = runCatching {
    val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
        ?: error("Could not create settings URL")
    val app = UIApplication.sharedApplication
    if (app.canOpenURL(url)) {
        app.openURL(url, options = emptyMap<Any?, Any>(), completionHandler = null)
    } else {
        error("Cannot open settings URL")
    }
}
