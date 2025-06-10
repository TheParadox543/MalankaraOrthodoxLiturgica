plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    // Google services plugin
    id("com.google.gms.google-services")
    // Add the Crashlytics plugin
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.paradox543.malankaraorthodoxliturgica"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.paradox543.malankaraorthodoxliturgica"
        minSdk = 24
        targetSdk = 35
        versionCode = 15
        versionName = "0.3.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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
    }
}

dependencies {
    // Core AndroidX & Kotlin Extensions
    implementation(libs.androidx.core.ktx)            // Core Android system utilities with Kotlin extensions
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle-aware components for Kotlin coroutines

    // Jetpack Compose UI
    implementation(libs.androidx.activity.compose)    // Compose integration for Activity
    implementation(platform(libs.androidx.compose.bom)) // BOM for consistent Compose library versions
    implementation(libs.androidx.ui)                  // Core Compose UI toolkit
    implementation(libs.androidx.ui.graphics)         // Compose graphics primitives
    implementation(libs.androidx.material3)           // Material Design 3 components for Compose

    // Jetpack Navigation
    implementation(libs.androidx.navigation.runtime.ktx) // Core Navigation library for Kotlin
    implementation(libs.androidx.navigation.compose)  // Navigation integration for Compose

    // Data Serialization
    implementation(libs.kotlinx.serialization.json) // Kotlinx Serialization library for JSON

    // Dependency Injection
    implementation(libs.hilt.android)                 // Dagger Hilt for Android dependency injection
    implementation(libs.androidx.hilt.navigation.compose) // Hilt integration with Jetpack Compose Navigation
    ksp(libs.hilt.android.compiler)                   // KSP annotation processor for Hilt

    // Data Storage
    implementation(libs.androidx.datastore.preferences) // Jetpack DataStore for preferences

    // Firebase Services
    implementation(platform(libs.firebase.bom))       // Firebase Bill of Materials for version consistency
    implementation(libs.firebase.analytics)           // Firebase Analytics for app usage data
    implementation(libs.firebase.crashlytics)         // Firebase Crashlytics for crash reporting

    // Testing Dependencies
    testImplementation(libs.junit)                    // Standard JUnit 4 for local unit tests
    androidTestImplementation(libs.androidx.junit)    // JUnit extensions for Android instrumented tests
    androidTestImplementation(libs.androidx.espresso.core) // Espresso for UI testing
    androidTestImplementation(platform(libs.androidx.compose.bom)) // BOM for Compose testing libs
    androidTestImplementation(libs.androidx.ui.test.junit4) // Compose testing rules for JUnit 4

    // Debugging & Development Tools (only for debug builds)
    debugImplementation(libs.androidx.ui.tooling)     // Compose tooling for previews and inspection
    debugImplementation(libs.androidx.ui.test.manifest) // Compose test manifest for UI testing
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}