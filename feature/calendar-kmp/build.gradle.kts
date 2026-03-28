plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
}

kotlin {
    androidLibrary {
        namespace = "com.paradox543.malankaraorthodoxliturgica.feature.calendar"

        compileSdk = providers.gradleProperty("COMPILE_SDK").get().toInt()
        minSdk = providers.gradleProperty("MIN_SDK").get().toInt()

        packaging {
            resources {
                excludes += "META-INF/LICENSE.md"
                excludes += "META-INF/LICENSE-notice.md"
                excludes += "META-INF/AL2.0"
                excludes += "META-INF/LGPL2.1"
                excludes += "META-INF/LICENSE"
                excludes += "META-INF/NOTICE"
                excludes += "META-INF/LICENSE.txt"
                excludes += "META-INF/NOTICE.txt"
            }
        }

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    val xcfName = "FeatureCalendarKmpKit"

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
                implementation(project(":core:ui-common"))

                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)
                // Dependency injection
                implementation(libs.koin.core)
                implementation(libs.koin.compose.viewmodel)
                // Compose Multiplatform dependencies
                implementation(libs.runtime)
                implementation(libs.foundation)
                // Icons dependency
                implementation(libs.icons.material.icons.rounded.cmp)

                implementation(libs.material3)
                implementation(libs.ui)
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
                implementation(libs.androidx.ui.test.manifest)
            }
        }

        getByName("androidHostTest") {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.junit)
                implementation(libs.mockk)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.junit)
                implementation(libs.mockk)
                implementation(libs.androidx.compose.ui.test.junit4)
            }
        }

        iosMain {
            dependencies {
            }
        }
    }
}