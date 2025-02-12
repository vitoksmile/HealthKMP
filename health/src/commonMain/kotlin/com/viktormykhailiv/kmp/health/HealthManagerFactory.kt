package com.viktormykhailiv.kmp.health

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class HealthManagerFactory() {

    fun createManager(): HealthManager
}