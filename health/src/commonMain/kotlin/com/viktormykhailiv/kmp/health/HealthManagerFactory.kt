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
     *
     * @see [HealthManagerFactoryOptions]
     */
    fun createManager(
        options: HealthManagerFactoryOptions = HealthManagerFactoryOptions.default(),
    ): HealthManager


}

/**
 * Configuration options used by [HealthManagerFactory] to customize the creation of a [HealthManager].
 *
 * This is an [expect] class with platform-specific implementations, allowing for
 * platform-specific configuration (e.g., specific data types or permissions).
 */
expect class HealthManagerFactoryOptions {

    companion object {
        fun default(): HealthManagerFactoryOptions
    }
}
