import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.binaryCompatibilityValidator)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.skie)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.get()))
        }
    }

    val xcframeworkName = "HealthKMP"
    val xcf = XCFramework(xcframeworkName)
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        val bundleId = "${providers.gradleProperty("GROUP").get()}.$xcframeworkName"
        it.binaries.framework {
            binaryOption("bundleId", bundleId)
            baseName = xcframeworkName
            xcf.add(this)
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines)
            api(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            implementation(libs.androidx.startup.runtime)

            // Google Fit
            implementation(libs.playservices.auth)
            implementation(libs.playservices.fitness)

            // Health Connect
            implementation(libs.androidx.healthconnect.client)
        }
    }
}

android {
    namespace = "com.viktormykhailiv.kmp.health"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        val javaVersion = JavaVersion.toVersion(libs.versions.java.get())
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}

task("printVersionName") {
    println(providers.gradleProperty("VERSION_NAME").get())
}

apiValidation {
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
}
