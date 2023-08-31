package com.vitoksmile.kmp.health

expect class HealthManagerFactory() {

    fun createManager(): HealthManager
}