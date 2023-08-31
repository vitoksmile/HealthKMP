@file:Suppress("unused")

package com.vitoksmile.kmp.health

import android.content.Context
import androidx.startup.Initializer

object ApplicationContextHolder {
    lateinit var applicationContext: Context
}

class ApplicationContextHolderInitializer : Initializer<ApplicationContextHolder> {

    override fun create(context: Context): ApplicationContextHolder {
        ApplicationContextHolder.applicationContext = context
        return ApplicationContextHolder
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        // No dependencies on other libraries.
        return emptyList()
    }
}