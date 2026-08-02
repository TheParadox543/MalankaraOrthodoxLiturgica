plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.lint)
}

kotlin {
    androidLibrary {
        namespace = "com.paradox543.malankaraorthodoxliturgica.shared"
        compileSdk {
            version =
                release(36) {
                    minorApiLevel = 1
                }
        }
        minSdk = 24

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    val xcfName = "sharedKit"

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.framework {
            baseName = xcfName

            export(project(":core:domain"))
            export(project(":data:prayer"))
            export(project(":feature:prayer-kmp"))
            export(project(":analytics:firebase"))
        }
    }
//    iosX64 {
//        binaries.framework {
//            baseName = xcfName
//
//            export(project(":core:domain"))
//            export(project(":data:prayer"))
//            export(project(":feature:prayer-kmp"))
//        }
//    }
//    iosArm64 {
//        binaries.framework {
//            baseName = xcfName
//
//            export(project(":core:domain"))
//            export(project(":data:prayer"))
//            export(project(":feature:prayer-kmp"))
//        }
//    }
//    iosSimulatorArm64{
//        binaries.framework {
//            baseName = xcfName
//
//            export(project(":core:domain"))
//            export(project(":data:prayer"))
//            export(project(":feature:prayer-kmp"))
//        }
//    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":core:domain"))
                api(project(":core:di"))
                api(project(":core:logging"))
                api(project(":core:ui-common"))
                api(project(":core:platform-kmp"))
                api(project(":core:app-info"))
                api(project(":analytics:firebase"))
                api(project(":data:core"))
                api(project(":data:sync"))
                api(project(":data:bible"))
                api(project(":data:calendar"))
                api(project(":data:prayer"))
                api(project(":data:settings"))
                api(project(":data:translations"))
                api(project(":feature:prayer-kmp"))
                api(project(":feature:calendar-kmp"))
                api(project(":feature:bible-kmp"))
                api(project(":feature:settings-kmp"))
                api(project(":feature:onboarding-kmp"))

                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.runtime)
                implementation(libs.foundation)
                implementation(libs.ui)
                implementation(libs.material3)
                implementation(libs.components.resources)
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