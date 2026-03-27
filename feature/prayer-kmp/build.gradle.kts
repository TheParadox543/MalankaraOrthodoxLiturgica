plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
}

kotlin {
    androidLibrary {
        namespace = "com.paradox543.malankaraorthodoxliturgica.feature.prayer"
        compileSdk = providers.gradleProperty("COMPILE_SDK").get().toInt()
        minSdk = providers.gradleProperty("MIN_SDK").get().toInt()
        androidResources.enable = true

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    val xcfName = "FeaturePrayerKmpKit"

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
                implementation(project(":core:analytics"))
                implementation(project(":core:platform-kmp"))
                implementation(project(":core:ui-common"))

                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)
                // Dependency injection
                implementation(libs.koin.core)
                implementation(libs.koin.compose.viewmodel)
                // Compose Multiplatform dependencies
                implementation(libs.runtime)
                implementation(libs.foundation)

                implementation(libs.material3)
                implementation(libs.compose.material3.adaptive)
                implementation(libs.compose.material3.adaptive.layout)
                implementation(libs.compose.material3.adaptive.navigation)

                implementation(libs.ui)
                implementation(libs.ui.tooling.preview)
                implementation(libs.components.resources)
                implementation(libs.compose.navigation)

                implementation(libs.androidx.lifecycle.runtime.compose)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
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
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.paradox543.malankaraorthodoxliturgica.feature.prayer"
    generateResClass = auto
}
