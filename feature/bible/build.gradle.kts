plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.paradox543.malankaraorthodoxliturgica.feature.bible"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
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
    }
}

dependencies {
    // Project imports
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(project(":core:platform"))

    implementation(project(":qr"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Dependency Injection
    implementation(libs.koin.core)
    implementation(libs.koin.compose.viewmodel)

    // Jetpack Compose UI
    implementation(libs.androidx.activity.compose)    // Compose integration for Activity
    implementation(platform(libs.androidx.compose.bom)) // BOM for consistent Compose library versions
    implementation(libs.androidx.ui)                  // Core Compose UI toolkit
    implementation(libs.androidx.ui.graphics)         // Compose graphics primitives
    implementation(libs.androidx.material3)           // Material Design 3 components for Compose

    testImplementation(libs.kotlin.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}