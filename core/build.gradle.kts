@file:Suppress("UNUSED_VARIABLE")

import java.util.Properties

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("maven-publish")
}

group = "com.vitoksmile.health-kmp"
version = "0.0.1"

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

kotlin {
    androidTarget {
        publishLibraryVariants("release", "debug")
        publishLibraryVariantsGroupedByFlavor = true
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        name = "HealthKMP"
        version = "0.0.1"
        summary = "Wrapper for HealthKit on iOS and Google Fit and Health Connect on Android."
        homepage = "https://github.com/vitoksmile/HealthKMP"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "HealthKMP"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("androidx.activity:activity:1.7.2")
                implementation("androidx.startup:startup-runtime:1.1.1")

                implementation("androidx.health.connect:connect-client:1.1.0-alpha03")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                // Fix ios build
                implementation("org.jetbrains.kotlinx:atomicfu:0.21.0")
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.vitoksmile.kmp.health"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")

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
