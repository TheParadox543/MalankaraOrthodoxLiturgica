plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.paradox543.malankaraorthodoxliturgica.data.calendar"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 26

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
        unitTests.all {
            it.jvmArgs("-Djdk.attach.allowAttachSelf=true")
        }
    }
}

dependencies {
    // Project dependencies
    implementation(project(":core:domain"))
    implementation(project(":data:core"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Dependency Injection
    implementation(libs.koin.core)

    // Data Serialization
    implementation(libs.kotlinx.serialization.json) // Kotlinx Serialization library for JSON

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.test.junit)
    testImplementation(libs.kotlinx.test.annotations.common)
    testImplementation(libs.mockk)
}