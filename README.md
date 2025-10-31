<img src=readme/app-logo.jpg width=256 />

# HealthKMP

[![Maven Central](https://img.shields.io/maven-central/v/com.viktormykhailiv/health-kmp)](https://central.sonatype.com/search?namespace=com.viktormykhailiv&name=health-kmp)
[![Kotlin](https://img.shields.io/badge/kotlin-2.2.10-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![](https://img.shields.io/badge/Kotlin-Multiplatform-%237f52ff?logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![](http://img.shields.io/badge/platform-android-6EDB8D?style=flat)](https://kotlinlang.org/docs/multiplatform.html)
[![](http://img.shields.io/badge/platform-ios-CDCDCD?style=flat)](https://kotlinlang.org/docs/multiplatform.html)
[![](http://img.shields.io/badge/platform-watchos-C0C0C0?style=flat)](https://kotlinlang.org/docs/multiplatform.html)

Kotlin Multiplatform wrapper for Apple's HealthKit (iOS and watchOS), and Google Fit or Health Connect on Android.

> Google Fitness API is being deprecated and HealthKMP will try to use Health Connect if the app is installed.

HealthKMP supports:
- checking if any of health service is available on the device.
- handling permissions to access health data.
- reading health data.
- writing health data.
- aggregating health data.

Note that for Android, the target device **needs** to have [Google Fit](https://www.google.com/fit/) or [Health Connect](https://health.google/health-connect-android/) installed.

## Supported data types (iOS, watchOS, Android)

- Blood glucose
- Blood pressure
- Body fat
- Body temperature
- Cycling pedaling cadence
- Exercise (segments, laps, routes)
- Heart rate
- Height
- Lean body mass
- Power
- Sleep
- Steps
- Weight

## Supported units

- Blood glucose: mmol/L, mg/dL
- Length: meters, kilometers, miles, inches, feet
- Mass: grams, kilograms, milligrams, micrograms, ounces, pounds
- Power: watts, kilocalories per day
- Percentage: e.g. 100%, 89.62%
- Pressure: millimeters of Mercury (mmHg)
- Temperature: Celsius, Fahrenheit

## Supported regional preferences

- Temperature: Celsius, Fahrenheit

## Requesting permission

To access health data users need to grant permissions

<img src=readme/permission-android.png height=512 /> <img src=readme/permission-ios.png height=512 />

<img src=readme/permission-watchos-1.png width=200 /> <img src=readme/permission-watchos-2.png width=200 /> <img src=readme/permission-watchos-3.png width=200 />

# Sample apps

Compose Multiplatform is used for iOS and Android apps.

SwiftUI is used for watchOS app.

# Setup

First add the dependency to your project:

settings.gradle.kts:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
```

build.gradle:

```kotlin
sourceSets {
    val commonMain by getting {
        dependencies {
            implementation("com.viktormykhailiv:health-kmp:0.0.15")
        }
    }
}
```

or use version catalog:

```
[versions]
health = "0.0.15"

[libraries]
health = { module = "com.viktormykhailiv:health-kmp", version.ref = "health" }

[plugins]
```

build.gradle:

```
implementation(libs.health)
```

## Apple Health (iOS or watchOS)

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
The permissions can be found here: https://developer.android.com/health-and-fitness/guides/health-connect/plan/data-types

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

### Check availability

Check if any Health service is available on the device: HealthKit on iOS or watchOS, and Google Fit or Health Connect on Android

```kotlin
val health = HealthManagerFactory().createManager()

health.isAvailable()
    .onSuccess { isAvailable ->
        if (!isAvailable) {
            println("No Health service is available on the device")
        }
    }
    .onFailure { error ->
        println("Failed to check if Health service is available $error")
    }
```

### Request access

Requesting access to data types before reading them

```kotlin
health.requestAuthorization(
    readTypes = listOf(
        BloodGlucose,
        BloodPressure,
        HeartRate,
        Height,
        Sleep,
        Steps,
        Weight,
    ),
    writeTypes = listOf(
        BloodGlucose,
        BloodPressure,
        HeartRate,
        Height,
        Sleep,
        Steps,
        Weight,
    ),
)
    .onSuccess { isAuthorized ->
        if (!isAuthorized) {
            println("Not authorized")
        }
    }
    .onFailure { error ->
        println("Failed to authorize $error")
    }
```

### Read sleep

Read detailed sleep data for last 24 hours

```kotlin
health.readSleep(
    startTime = Clock.System.now().minus(24.hours),
    endTime = Clock.System.now(),
).onSuccess { sleepRecords ->
    sleepRecords.forEach { sleep ->
        println("Sleep duration ${sleep.duration}")

        // Calculate duration of each sleep stage
        sleep.stages.groupBy { it.type }
            .forEach { (type, stages) ->
                val stageDuration = stages.sumOf { it.duration.inWholeMinutes }.minutes
                println("Sleep stage $type $stageDuration")
            }
    }
    if (sleepRecords.isEmpty()) {
        println("No sleep data")
    }
}.onFailure { error ->
    println("Failed to read sleep $error")
}
```

Read aggregated sleep data for last month

```kotlin
health.aggregateSleep(
    startTime = Clock.System.now().minus(30.days),
    endTime = Clock.System.now(),
).onSuccess { sleep ->
    println("Sleep total duration ${sleep.totalDuration}")
}
```

### Read steps

Read detailed steps data for last day

```kotlin
health.readSteps(
    startTime = Clock.System.now().minus(1.days),
    endTime = Clock.System.now(),
).onSuccess { steps ->
    steps.forEachIndexed { index, record ->
        println("[$index] ${record.count} steps for ${record.duration}")
    }
    if (steps.isEmpty()) {
        println("No steps data")
    }
}.onFailure { error ->
    println("Failed to read steps $error")
}
```

Read aggregated steps data for last day

```kotlin
health.aggregateSteps(
    startTime = Clock.System.now().minus(1.days),
    endTime = Clock.System.now(),
).onSuccess { steps ->
    println("Steps total ${steps.count}")
}
```

### Read weight

Read detailed weight data for last year

```kotlin
health.readWeight(
    startTime = Clock.System.now().minus(365.days),
    endTime = Clock.System.now(),
).onSuccess { records ->
    records.forEachIndexed { index, record ->
        println("[$index] ${record.weight} at ${record.time}")
    }
    if (records.isEmpty()) {
        println("No weight data")
    }
}.onFailure { error ->
    println("Failed to read weight $error")
}
```

Read aggregated weight data for last year

```kotlin
health.aggregateWeight(
    startTime = Clock.System.now().minus(365.hours),
    endTime = Clock.System.now(),
).onSuccess { weight ->
    println("Weight avg ${weight.avg} kg, min ${weight.min}, max ${weight.max}")
}
```

### Write sleep

Write sleep data for 1 hours

```kotlin
val startTime = Clock.System.now().minus(12.hours)
val endTime = Clock.System.now().minus(11.hours)
val types = listOf(
    SleepStageType.Awake,
    SleepStageType.OutOfBed,
    SleepStageType.Sleeping,
    SleepStageType.Light,
    SleepStageType.Deep,
    SleepStageType.REM,
)
health.writeData(
    records = listOf(
        SleepSessionRecord(
            startTime = startTime,
            endTime = endTime,
            stages = List(6) {
                SleepSessionRecord.Stage(
                    startTime = startTime.plus((10 * it).minutes),
                    endTime = startTime.plus((10 * it).minutes + 10.minutes),
                    type = types[it],
                )
            },
            metadata = generateMetadata(),
        )
    )
)
```

### Write steps

```kotlin
health.writeData(
    records = listOf(
        StepsRecord(
            startTime = Clock.System.now().minus(1.days).minus(3.hours),
            endTime = Clock.System.now().minus(1.days).minus(1.hours),
            count = 75,
            metadata = generateMetadata(),
        ),
        StepsRecord(
            startTime = Clock.System.now().minus(1.hours),
            endTime = Clock.System.now(),
            count = 123,
            metadata = generateMetadata(),
        ),
    )
)
```

### Write weight

There are different supported `Mass` units: kilograms, ounces, pounds, grams, milligrams, micrograms.

```kotlin
health.writeData(
    records = listOf(
        // Weight in kilograms
        WeightRecord(
            time = Clock.System.now().minus(1.days),
            weight = Mass.kilograms(61.2),
            metadata = generateMetadata(),
        ),
        // Weight in pounds
        WeightRecord(
            time = Clock.System.now(),
            weight = Mass.pounds(147.71),
            metadata = generateMetadata(),
        ),
    )
)
```

### Metadata

`HealthRecord` requires `Metadata` with recording method to help to understand how the data was
recorded, unique identifier of data, and device information associated with the data.

```kotlin
fun generateMetadata(): Metadata {
    return Metadata.manualEntry(
        id = Uuid.random().toString(),
        device = Device.getLocalDevice(),
    )
}
```

## Swift

HealthKMP is Swift compatible and can be added as package dependency to Xcode projects.

### Add the package dependency

1. In Xcode, choose **File | Add Package Dependencies**.

2. In the search field, enter `https://github.com/vitoksmile/HealthKMP-SPM`:

   <img src=readme/swift-package-manager.png width=640 />

3. Press the **Add package** button.
