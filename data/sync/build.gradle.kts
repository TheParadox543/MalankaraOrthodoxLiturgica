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
        namespace = "com.paradox543.malankaraorthodoxliturgica.data.sync"
        compileSdk = providers.gradleProperty("COMPILE_SDK").get().toInt()
        minSdk = providers.gradleProperty("MIN_SDK").get().toInt()

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    val xcfName = "DataSyncKit"

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
                implementation(project(":core:domain"))
                implementation(project(":data:core"))

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.koin.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.components.resources)
                implementation(libs.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.firebase.storage)
                implementation(libs.kotlinx.coroutines.play.services)
            }
        }

        iosMain {
            dependencies {
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.paradox543.malankaraorthodoxliturgica.data.sync"
    generateResClass = auto
}

dependencies {
    "androidMainImplementation"(platform(libs.firebase.bom))
}
