package com.viktormykhailiv.kmp.health

import androidx.compose.runtime.staticCompositionLocalOf

val LocalHealthManager = staticCompositionLocalOf<HealthManager> {
    error("HealthManager not provided")
}
