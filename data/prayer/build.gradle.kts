plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
}

kotlin {
    androidLibrary {
        namespace = "com.paradox543.malankaraorthodoxliturgica.data.prayer"
        compileSdk = providers.gradleProperty("COMPILE_SDK").get().toInt()
        minSdk = providers.gradleProperty("MIN_SDK").get().toInt()

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        androidResources {
            enable = true
            // TODO: "Need to add resources so that it works in iOS as well"
        }
    }

    val xcfName = "DataPrayerKit"

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
                // Project imports
                implementation(project(":core:domain"))
                implementation(project(":data:core"))

                implementation(libs.kotlinx.coroutines.core)
                // Dependency Injection
                implementation(libs.koin.core)

                // Data Serialization
                implementation(libs.kotlinx.serialization.json)

                // Shared resources
                implementation(libs.components.resources)
                implementation(libs.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                // Add Android-specific dependencies here. Note that this source set depends on
                // commonMain by default and will correctly pull the Android artifacts of any KMP
                // dependencies declared in commonMain.
            }
        }

        getByName("androidHostTest") {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.mockk)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.junit)
            }
        }

        iosMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.paradox543.malankaraorthodoxliturgica.data.prayer"
    generateResClass = auto
}
