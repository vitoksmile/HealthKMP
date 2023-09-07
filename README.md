# HealthKMP

[![](https://img.shields.io/badge/Kotlin-Multiplatform-%237f52ff?logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![](https://img.shields.io/github/license/vitoksmile/HealthKMP)](https://github.com/vitoksmile/HealthKMP/blob/main/LICENSE.txt)

Kotlin Multiplatform Mobile wrapper for HealthKit on iOS and ~~Google Fit and~~ Health Connect on Android.

> Google Fitness API is being deprecated and Health Connect the plugin will transition into the API as the Health Connect

The library supports:
- handling permissions to access health data using `isAvailable`, `isAuthorized`, `requestAuthorization`, `revokeAuthorization` methods.
- reading health data using `readData` method.
- writing health data using `writeData` method.

Note that for Android, the target phone **needs** to have ~~[Google Fit](https://www.google.com/fit/) or~~ [Health Connect](https://health.google/health-connect-android/) (which is currently in beta) installed and have access to the internet, otherwise this library will not work.

## Data Types
- STEPS
- WEIGHT

## Requesting permission

To access health data users need to grant permissions

<img src=images/permission-health-connect.png height=480 /> <img src=images/permission-health-kit.png height=480 />

# Setup

You need an access token to install GitHub packages, see [Managing your personal access tokens](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens).

local.properties
```
GITHUB_USERNAME=email@email.com
GITHUB_TOKEN=xxx
```

settings.gradle.kts:
```kotlin
dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/vitoksmile/HealthKMP")
            name = "GitHubPackages"
            credentials {
                val properties = java.util.Properties()
                properties.load(file("local.properties").inputStream())
                username = properties["GITHUB_USERNAME"].toString()
                password = properties["GITHUB_TOKEN"].toString()
            }
        }
    }
}
```

build.gradle:
```kotlin
sourceSets {
    val commonMain by getting {
        dependencies {
            implementation("com.vitoksmile.health-kmp:core:0.0.1")
        }
    }
}
```

If you are using Koin, add to build.gradle:
```kotlin
implementation("com.vitoksmile.health-kmp:koin:0.0.1")
```


## 👷 Project Structure
* <kbd>core</kbd> - module with main source for the HealthKMP library
* <kbd>koin</kbd> - module with extensions for Koin

* <kbd>sample</kbd> - shared code for sample Compose Multiplatform project
* <kbd>androidApp</kbd> - sample Android projects that use HealthKMP
* <kbd>iosApp</kbd> - sample iOS projects that use HealthKMP


## 📜 License

This project is licensed under the Apache License, Version 2.0 - see the [LICENSE.md](https://github.com/Foso/Ktorfit/blob/master/LICENSE) file for details