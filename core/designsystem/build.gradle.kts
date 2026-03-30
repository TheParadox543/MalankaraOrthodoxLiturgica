plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    androidLibrary {
        namespace = "com.paradox543.malankaraorthodoxliturgica.designsystem"
        compileSdk = providers.gradleProperty("COMPILE_SDK").get().toInt()
        minSdk = providers.gradleProperty("MIN_SDK").get().toInt()
        androidResources.enable = true
    }

    val xcfName = "CoreDesignSystemKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                // Compose Multiplatform dependencies
                implementation(libs.runtime)
                implementation(libs.foundation)
                implementation(libs.material3)
                implementation(libs.ui)
                implementation(libs.components.resources)

                // Dependency injection
//                implementation(libs.koin.core)
//                implementation(libs.koin.compose)

                // Google font
//                implementation(libs.androidx.ui.text.google.fonts)
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.paradox543.malankaraorthodoxliturgica.designsystem"
    generateResClass = auto
}
