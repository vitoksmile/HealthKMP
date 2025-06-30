package com.viktormykhailiv.kmp.health

expect class HealthManagerFactory() {

    fun createManager(): HealthManager
}