package com.vitoksmile.kmm.health.koin

import android.app.Application
import com.vitoksmile.kmm.health.ApplicationContextHolder
import org.koin.core.KoinApplication

fun KoinApplication.attachHealthKMM(
    application: Application,
) {
    ApplicationContextHolder.applicationContext = application
    modules(commonModule())
}