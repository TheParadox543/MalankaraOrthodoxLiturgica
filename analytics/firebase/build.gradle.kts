plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
}

kotlin {
    androidLibrary {
        namespace = "com.paradox543.malankaraorthodoxliturgica.analytics.firebase"
        compileSdk {
            version = release(36)
        }
        minSdk = 26
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain {
            dependencies {
                api(project(":core:analytics"))
                implementation(project(":core:app-info"))
                implementation(libs.koin.core)
            }
        }
        androidMain {
            dependencies {
                implementation(project(":core:app-info"))
                implementation(libs.koin.android)
                implementation(libs.firebase.analytics)
            }
        }
        iosMain {
            dependencies {
            }
        }
    }
}

dependencies {
    "androidMainImplementation"(platform(libs.firebase.bom))
}
