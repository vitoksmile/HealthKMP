package com.vitoksmile.kmm.health.koin

import com.vitoksmile.kmm.health.HealthManagerFactory
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun commonModule(): Module = module {
    single { HealthManagerFactory() }
}