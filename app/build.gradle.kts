import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Linter plugin
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    // Google services plugin
    id("com.google.gms.google-services")
    // Add the Crashlytics plugin
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.paradox543.malankaraorthodoxliturgica"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.paradox543.malankaraorthodoxliturgica"
        minSdk = 26
        targetSdk = 36
        versionCode = 52
        versionName = "2.2.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        versionNameSuffix = "-alpha.2"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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
    implementation(project(":shared"))
    implementation(project(":core:domain"))
    implementation(project(":data:bible"))
    implementation(project(":data:calendar"))
    implementation(project(":data:prayer"))
    implementation(project(":data:settings"))
    implementation(project(":data:translations"))
    implementation(project(":feature:prayer"))

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
    implementation(libs.hilt.android)                 // Dagger Hilt for Android dependency injection
    implementation(libs.androidx.hilt.navigation.compose) // Hilt integration with Jetpack Compose Navigation
    ksp(libs.hilt.android.compiler)                   // KSP annotation processor for Hilt

    // Background Work Management
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.work.runtime.ktx)

    // Data Storage
    implementation(libs.androidx.datastore.preferences) // Jetpack DataStore for preferences

    // QR generation and scanning
    implementation(libs.zxing.android.embedded)
    implementation(libs.zxing.core)
    implementation(libs.barcode.scanning)

    // Camera Scanning
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Firebase Services
    implementation(platform(libs.firebase.bom))       // Firebase Bill of Materials for version consistency
    implementation(libs.firebase.analytics)           // Firebase Analytics for app usage data
    implementation(libs.firebase.crashlytics)         // Firebase Crashlytics for crash reporting
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.firebase.storage)

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