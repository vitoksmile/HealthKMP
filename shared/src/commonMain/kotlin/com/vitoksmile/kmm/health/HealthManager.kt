package com.vitoksmile.kmm.health

interface HealthManager {

    fun isAvailable(): Result<Boolean>
}