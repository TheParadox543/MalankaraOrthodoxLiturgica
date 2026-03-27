plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
}

kotlin {
    androidLibrary {
        namespace = "com.paradox543.malankaraorthodoxliturgica.feature.settings"
        compileSdk = providers.gradleProperty("COMPILE_SDK").get().toInt()
        minSdk = providers.gradleProperty("MIN_SDK").get().toInt()
        androidResources.enable = true

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

    val xcfName = "FeatureSettingsKmpKit"

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

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    sourceSets {
        commonMain {
            dependencies {
                // Project Imports
                implementation(project(":core:domain"))
                implementation(project(":core:analytics"))
                implementation(project(":core:platform-kmp"))
                implementation(project(":core:ui-common"))
                implementation(project(":core:app-info"))

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
                // Icons dependency
                implementation(libs.icons.material.icons.rounded.cmp)

                implementation(libs.ui)
                // TODO this needs to be added, otherwise BackHandler build fails unresolved
                implementation(libs.compose.ui.backhandler)
                implementation(libs.ui.tooling.preview)
                implementation(libs.components.resources)
                implementation(libs.compose.navigation)

                implementation(libs.androidx.lifecycle.runtime.compose)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":core:platform-kmp"))
                implementation(project(":core:app-info"))
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
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

compose.resources {
    publicResClass = true
    packageOfResClass = "com.paradox543.malankaraorthodoxliturgica.feature.settings"
    generateResClass = auto
}
