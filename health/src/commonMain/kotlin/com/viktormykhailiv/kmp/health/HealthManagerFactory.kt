package com.viktormykhailiv.kmp.health

/**
 * Factory class for creating [HealthManager] instances.
 *
 * This is an [expect] class with platform-specific implementations.
 */
expect class HealthManagerFactory() {

    /**
     * Creates a new [HealthManager] instance for the current platform.
     *
     * @return A platform-specific implementation of [HealthManager].
     */
    fun createManager(): HealthManager
}