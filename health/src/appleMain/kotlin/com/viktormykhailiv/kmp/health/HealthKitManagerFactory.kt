package com.viktormykhailiv.kmp.health

/**
 * iOS-specific implementation of [HealthManagerFactory] that provides access to [HealthKitManager].
 *
 * This factory is responsible for creating [HealthManager] instances using Apple's HealthKit framework.
 * It utilizes [HealthManagerFactoryOptions] to configure the initialization process.
 */
actual class HealthManagerFactory {

    @Deprecated(
        "Use createManager with options",
        replaceWith = ReplaceWith(
            expression = "createManager(options = HealthManagerFactoryOptions.default())",
            imports = arrayOf("com.viktormykhailiv.kmp.health.HealthManagerFactoryOptions"),
        ),
    )
    fun createManager(): HealthManager =
        createManager(options = HealthManagerFactoryOptions.default())

    actual fun createManager(options: HealthManagerFactoryOptions): HealthManager =
        HealthKitManager()

}

/**
 * Configuration options for creating a [HealthManager] instance via [HealthManagerFactory].
 *
 * These options provide a way to customize the initialization and behavior of the health manager.
 * Use [default] to obtain a standard configuration instance.
 */
actual class HealthManagerFactoryOptions {

    actual companion object {
        actual fun default() = HealthManagerFactoryOptions()
    }
}
