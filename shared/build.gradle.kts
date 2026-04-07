plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
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

//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()
//
//    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
//        binaries.framework {
//            baseName = xcfName
//
//            export(project(":core:domain"))
//            export(project(":data:prayer"))
//            export(project(":feature:prayer-kmp"))
//        }
//    }
    iosX64 {
        binaries.framework {
            baseName = xcfName

            export(project(":core:domain"))
            export(project(":data:prayer"))
            export(project(":feature:prayer-kmp"))
        }
    }
    iosArm64 {
        binaries.framework {
            baseName = xcfName

            export(project(":core:domain"))
            export(project(":data:prayer"))
            export(project(":feature:prayer-kmp"))
        }
    }
    iosSimulatorArm64{
        binaries.framework {
            baseName = xcfName

            export(project(":core:domain"))
            export(project(":data:prayer"))
            export(project(":feature:prayer-kmp"))
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":core:domain"))
                api(project(":core:ui-common"))
                api(project(":data:prayer"))
                api(project(":feature:prayer-kmp"))
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