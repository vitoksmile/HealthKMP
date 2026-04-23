@file:Suppress("unused")

package com.viktormykhailiv.kmp.health

expect class HealthManagerFactory() {

    fun createManager(): HealthManager

    fun createManager(options: Options): HealthManager

    class Options
}