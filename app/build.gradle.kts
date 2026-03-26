import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)              // Linter plugin
    alias(libs.plugins.kotlin.serialization)
    // Google services plugin
    id("com.google.gms.google-services")
    // Add the Crashlytics plugin
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.paradox543.malankaraorthodoxliturgica"
    compileSdk = providers.gradleProperty("COMPILE_SDK").get().toInt()

    defaultConfig {
        applicationId = "com.paradox543.malankaraorthodoxliturgica"
        minSdk = providers.gradleProperty("MIN_SDK").get().toInt()
        targetSdk = 36
        versionCode = providers.gradleProperty("APP_VERSION_CODE").get().toInt()
        versionName = providers.gradleProperty("APP_VERSION_NAME").get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        versionNameSuffix = ""
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            ndk.debugSymbolLevel = "FULL"
            resValue("string", "app_name", "Liturgica")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            configure<CrashlyticsExtension> {
                // Enable processing and uploading of native symbols to Firebase servers.
                // By default, this is disabled to improve build speeds.
                // This flag must be enabled to see properly-symbolical native
                // stack traces in the Crashlytics dashboard.
                nativeSymbolUploadEnabled = true
            }
        }
        debug {
            applicationIdSuffix = ".testing"
            versionNameSuffix = "-dev"
            resValue("string", "app_name", "Liturgica (Dev)")
        }
    }
    packaging {
        jniLibs.keepDebugSymbols += arrayOf("**/*.so")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    ndkVersion = "29.0.13599879 rc2"
    buildToolsVersion = "35.0.0"
}

dependencies {
    // Project imports
    implementation(project(":core:domain"))
    implementation(project(":data:core"))
    implementation(project(":data:bible"))
    implementation(project(":data:calendar"))
    implementation(project(":data:prayer"))
    implementation(project(":data:settings"))
    implementation(project(":data:translations"))
    implementation(project(":data:song"))

    implementation(project(":qr"))

    implementation(project(":core:ui-common"))
    implementation(project(":core:app-info"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:settings-kmp"))
    implementation(project(":feature:onboarding-kmp"))
    implementation(project(":feature:prayer"))
    implementation(project(":feature:bible"))
    implementation(project(":feature:calendar"))
    implementation(project(":feature:song"))

    implementation(project(":core:analytics"))
    implementation(project(":core:platform"))
    implementation(project(":core:platform-kmp"))
    implementation(project(":analytics:firebase-android"))

    // Core AndroidX & Kotlin Extensions
    implementation(libs.androidx.core.ktx)            // Core Android system utilities with Kotlin extensions
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle-aware components for Kotlin coroutines

    // Jetpack Compose UI
    implementation(libs.androidx.activity.compose)    // Compose integration for Activity
    implementation(platform(libs.androidx.compose.bom)) // BOM for consistent Compose library versions
    implementation(libs.androidx.ui)                  // Core Compose UI toolkit
    implementation(libs.androidx.ui.graphics)         // Compose graphics primitives
    implementation(libs.androidx.material3)           // Material Design 3 components for Compose
    implementation(libs.androidx.core.splashscreen)  // Splashscreen API for Jetpack Compose

    // Jetpack Navigation
    implementation(libs.androidx.navigation.runtime.ktx) // Core Navigation library for Kotlin
    implementation(libs.androidx.navigation.compose)  // Navigation integration for Compose

    // Dependency Injection
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.androidx.compose.navigation)
    implementation(libs.koin.androidx.workmanager)

    // Background Work Management
    implementation(libs.androidx.work.runtime.ktx)

    // Data Storage
    implementation(libs.androidx.datastore.preferences) // Jetpack DataStore for preferences

    // Firebase Services
    implementation(platform(libs.firebase.bom))       // Firebase Bill of Materials for version consistency
    implementation(libs.firebase.analytics)           // Firebase Analytics for app usage data
    implementation(libs.firebase.crashlytics)         // Firebase Crashlytics for crash reporting
    implementation(libs.firebase.crashlytics.ndk)

    // Media Player
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.common)

    // For Google Play Core libraries
    implementation(libs.review.ktx)
    implementation(libs.app.update.ktx)

    // Google Fonts
    implementation(libs.androidx.ui.text.google.fonts)

    // Testing Dependencies
    testImplementation(libs.junit)                    // Standard JUnit 4 for local unit tests
    androidTestImplementation(libs.androidx.junit)    // JUnit extensions for Android instrumented tests
    androidTestImplementation(libs.androidx.espresso.core) // Espresso for UI testing
    androidTestImplementation(platform(libs.androidx.compose.bom)) // BOM for Compose testing libs
    androidTestImplementation(libs.androidx.ui.test.junit4) // Compose testing rules for JUnit 4

    // Debugging & Development Tools (only for debug builds)
    debugImplementation(libs.androidx.ui.tooling) // Compose tooling for previews and inspection
    implementation(libs.androidx.ui.tooling.preview)  // Compose tooling for viewing previews
    debugImplementation(libs.androidx.ui.test.manifest) // Compose test manifest for UI testing
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}