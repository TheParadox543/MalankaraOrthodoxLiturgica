plugins {
    kotlin("jvm")
    alias(libs.plugins.ktlint)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(kotlin("stdlib"))

    // If you use Flow
    implementation(libs.kotlinx.coroutines.core)

    // Unit testing
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
}
