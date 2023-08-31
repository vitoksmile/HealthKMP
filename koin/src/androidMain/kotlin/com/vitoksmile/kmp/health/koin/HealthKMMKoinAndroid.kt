package com.vitoksmile.kmp.health.koin

import android.app.Application
import com.vitoksmile.kmp.health.ApplicationContextHolder
import org.koin.core.KoinApplication

fun KoinApplication.attachHealthKMP(
    application: Application,
) {
    ApplicationContextHolder.applicationContext = application
    modules(commonModule())
}