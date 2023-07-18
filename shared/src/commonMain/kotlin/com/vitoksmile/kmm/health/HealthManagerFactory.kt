package com.vitoksmile.kmm.health

expect class HealthManagerFactory() {

    fun createManager(): HealthManager
}