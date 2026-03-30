plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.ktlint)
}

kotlin {
    androidLibrary {
        namespace = "com.paradox543.malankaraorthodoxliturgica.domain"
        compileSdk = providers.gradleProperty("COMPILE_SDK").get().toInt()
        minSdk = providers.gradleProperty("MIN_SDK").get().toInt()
    }

    // Keep iOS targets if domain should be shared with iosApp
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}