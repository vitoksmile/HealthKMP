package com.vitoksmile.kmm.health

import org.koin.dsl.module

actual val healthManagerFactoryModule = module {
    single { HealthManagerFactory() }
}