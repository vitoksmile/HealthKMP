package com.vitoksmile.kmm.health

import org.koin.core.module.Module
import org.koin.dsl.module

expect val healthManagerFactoryModule: Module

fun commonModule(): Module = module {
    includes(healthManagerFactoryModule)
}