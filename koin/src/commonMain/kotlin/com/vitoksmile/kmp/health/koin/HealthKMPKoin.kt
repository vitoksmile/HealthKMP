package com.vitoksmile.kmp.health.koin

import com.vitoksmile.kmp.health.HealthManagerFactory
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun commonModule(): Module = module {
    single { HealthManagerFactory() }
}