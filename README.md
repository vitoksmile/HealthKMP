# HealthKMP

[![](https://img.shields.io/badge/Kotlin-Multiplatform-%237f52ff?logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![](https://img.shields.io/github/license/vitoksmile/HealthKMP)](https://github.com/vitoksmile/HealthKMP/blob/main/LICENSE)

Kotlin Multiplatform Mobile wrapper for HealthKit on iOS and Google Fit and Health Connect on Android.

> Google Fitness API is being deprecated and HealthKMP will try to use Health Connect if the app is installed.

The library supports:
- handling permissions to access health data using `isAvailable`, `isAuthorized`, `requestAuthorization`, `revokeAuthorization` methods.
- reading health data using `readData` method.
- writing health data using `writeData` method.

Note that for Android, the target phone **needs** to have [Google Fit](https://www.google.com/fit/) or [Health Connect](https://health.google/health-connect-android/) (which is currently in beta) installed and have access to the internet, otherwise this library will not work.

## Data Types
- Steps
- Weight

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
            implementation("com.vitoksmile.health-kmp:core:0.0.3")
        }
    }
}
```

If you are using Koin, add to build.gradle:
```kotlin
implementation("com.vitoksmile.health-kmp:koin:0.0.3")
```

## Apple Health (iOS)

Step 1: Append the Info.plist with the following 2 entries

```xml
<key>NSHealthShareUsageDescription</key>
<string>We will sync your data with the Apple Health app to give you better insights</string>
<key>NSHealthUpdateUsageDescription</key>
<string>We will sync your data with the Apple Health app to give you better insights</string>
```

Step 2: Enable "HealthKit" by adding a capability inside the "Signing & Capabilities" tab of the Runner target's settings.

## Health Connect (Android option 1)

Using Health Connect on Android requires special permissions in the `AndroidManifest.xml` file.
The permissions can be found here: https://developer.android.com/guide/health-and-fitness/health-connect/data-and-data-types/data-types

Example shown here (can also be found in the sample app):

```
<uses-permission android:name="android.permission.health.READ_HEART_RATE"/>
<uses-permission android:name="android.permission.health.WRITE_HEART_RATE"/>
```

## Google Fit (Android option 2)

Follow the guide at https://developers.google.com/fit/android/get-api-key

Below is an example of following the guide:

Change directory to your key-store directory (MacOS):
`cd ~/.android/`

Get your keystore SHA1 fingerprint:
`keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android`

Example output:

```
Alias name: androiddebugkey
Creation date: Aug 8, 2023
Entry type: PrivateKeyEntry
Certificate chain length: 1
Certificate[1]:
Owner: C=US, O=Android, CN=Android Debug
Issuer: C=US, O=Android, CN=Android Debug
Serial number: 4aa9b300
Valid from: Tue Aug 01 10:07:15 CEST 2023 until: Thu Jul 24 10:07:15 CEST 2053
Certificate fingerprints:
     MD5:  E4:D7:F1:B4:ED:2E:42:D1:58:98:F4:B2:7B:01:9D:A4
     SHA1: E4:D7:F1:B4:ED:2E:42:D1:58:98:F4:B2:7B:01:9D:A4:E4:D7:F1:B4
Signature algorithm name: SHA256withRSA
Subject Public Key Algorithm: 2048-bit RSA key
Version: 1
```

Follow the instructions at https://developers.google.com/fit/android/get-api-key for setting up an OAuth2 Client ID for a Google project, and adding the SHA1 fingerprint to that OAuth2 credential.

The client id will look something like `YOUR_CLIENT_ID.apps.googleusercontent.com`.

## Usage

See the sample app for detailed examples of how to use the HealthKMP API.

The Health plugin is used via the `HealthManagerFactory` class using the different methods for handling permissions and getting and adding data to Apple Health / Health Connect / Google Fit.

```kotlin
val health = HealthManagerFactory().createManager()

// Check if any Health service is available on the device
health.isAvailable()
    .onSuccess { isAvailable ->
        if (!isAvailable) {
            println("No Health service is available on the device")
            return
        }
    }
    .onFailure { error ->
        println("Failed to authorize $error")
        return
    }

// Requesting access to data types before reading them
health.requestAuthorization(
    readTypes = listOf(
        HealthDataType.Steps,
        HealthDataType.Weight,
    ),
    writeTypes = listOf(
        HealthDataType.Steps,
        HealthDataType.Weight,
    ),
)
    .onSuccess { isAuthorized ->
        if (!isAuthorized) {
            println("Not authorized")
            return
        }
    }
    .onFailure { error ->
        println("Failed to authorize $error")
        return
    }

// Fetch steps data from the last 24 hours
health.readSteps(
    startTime = Clock.System.now().minus(24.hours),
    endTime = Clock.System.now(),
)
    .onSuccess { steps ->
        if (steps.isEmpty()) {
            println("No steps data")
        } else {
            val average = steps.map { it.count }.average()
            val total = steps.sumOf { it.count }
            println("Steps avg $average, total $total")
        }
    }
    .onFailure { error ->
        println("Failed to read steps $error")
    }

// Fetch weight data from the last year
health.readWeight(
    startTime = Clock.System.now().minus(365.days),
    endTime = Clock.System.now(),
)
    .onSuccess { records ->
        if (records.isEmpty()) {
            println("No weight data")
        } else {
            val average = records.map { it.weight.inKilograms }.average()
            val min = records.minOf { it.weight.inKilograms }
            val max = records.maxOf { it.weight.inKilograms }
            println("Weight avg $average kg, min $min kg, max $max kg")
        }
    }
    .onFailure { error ->
        println("Failed to read steps $error")
    }

// Write data
health.writeData(
    records = listOf(
        StepsRecord(
            startTime = Clock.System.now().minus(1.days).minus(3.hours),
            endTime = Clock.System.now().minus(1.days).minus(1.hours),
            count = 75,
        ),
        StepsRecord(
            startTime = Clock.System.now().minus(1.hours),
            endTime = Clock.System.now(),
            count = 123,
        ),
        WeightRecord(
            time = Clock.System.now().minus(1.days),
            weight = Mass.kilograms(61.2),
        ),
        WeightRecord(
            time = Clock.System.now(),
            weight = Mass.kilograms(60.3),
        ),
    )
)
```

## 👷 Project Structure
* <kbd>core</kbd> - module with main source for the HealthKMP library
* <kbd>koin</kbd> - module with extensions for Koin

* <kbd>sample</kbd> - shared code for sample Compose Multiplatform project
* <kbd>androidApp</kbd> - sample Android projects that use HealthKMP
* <kbd>iosApp</kbd> - sample iOS projects that use HealthKMP

## 📜 License

This project is licensed under the Apache License, Version 2.0 - see the [LICENSE](https://github.com/vitoksmile/HealthKMP/blob/main/LICENSE) file for details