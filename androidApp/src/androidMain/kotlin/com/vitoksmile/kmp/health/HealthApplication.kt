package com.vitoksmile.kmp.health

import android.app.Application
import com.vitoksmile.kmp.health.koin.attachHealthKMP
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class HealthApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@HealthApplication)
            androidLogger()
            attachHealthKMP(this@HealthApplication)
        }
    }
}