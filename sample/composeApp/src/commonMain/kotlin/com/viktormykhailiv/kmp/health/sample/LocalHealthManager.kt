package com.viktormykhailiv.kmp.health.sample

import androidx.compose.runtime.staticCompositionLocalOf
import com.viktormykhailiv.kmp.health.HealthManager

val LocalHealthManager = staticCompositionLocalOf<HealthManager> {
    error("HealthManager not provided")
}
