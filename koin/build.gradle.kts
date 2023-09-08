@file:Suppress("UNUSED_VARIABLE", "PropertyName")

import java.util.Properties

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("maven-publish")
}

group = "com.vitoksmile.health-kmp"
version = "0.0.2"

publishing {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/vitoksmile/HealthKMP")
            name = "GitHubPackages"
            credentials {
                val properties = Properties()
                properties.load(project.rootProject.file("local.properties").inputStream())
                username = properties["GITHUB_USERNAME"].toString()
                password = properties["GITHUB_TOKEN"].toString()
            }
        }
    }
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    androidTarget {
        publishLibraryVariants("release", "debug")
        publishLibraryVariantsGroupedByFlavor = true
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        name = "HealthKMPKoin"
        version = "0.0.2"
        summary =
            "Shared Koin module for wrapper for HealthKit on iOS and Google Fit and Health Connect on Android."
        homepage = "https://github.com/vitoksmile/HealthKMP"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "HealthKMPKoin"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.vitoksmile.health-kmp:core:0.0.2")

                api("io.insert-koin:koin-core:3.4.0")
            }
        }

        val androidMain by getting {
            dependencies {
                api("io.insert-koin:koin-android:3.3.3")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by getting {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.vitoksmile.kmp.health.koin"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}