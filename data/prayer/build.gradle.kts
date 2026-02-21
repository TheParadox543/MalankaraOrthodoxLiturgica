plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktlint)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.paradox543.malankaraorthodoxliturgica.data.prayer"
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
            // Required: PrayerElementSerializer calls android.util.Log.d in its catch block,
            // and AssetJsonReader calls Log.e. Without this, JVM unit tests throw
            // "Method not mocked" for any Android stub that gets touched.
            isReturnDefaultValues = true
            all {
                // Suppress the ByteBuddy/mockk "Java agent loaded dynamically" warning on JDK 21+
                it.jvmArgs("-Djdk.attach.allowAttachSelf=true")
            }
        }
    }
}

dependencies {
    // Project imports
    implementation(project(":core:domain"))
    implementation(project(":data:core"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Dependency Injection
    implementation(libs.hilt.android)                 // Dagger Hilt for Android dependency injection
    ksp(libs.hilt.android.compiler)                   // KSP annotation processor for Hilt

    // Data Serialization
    implementation(libs.kotlinx.serialization.json) // Kotlinx Serialization library for JSON

    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}