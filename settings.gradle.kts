pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MalankaraOrthodoxLiturgica"
include(":app")
include(":core:domain")
include(":core:analytics")
include(":core:platform")
include(":data:core")
include(":data:bible")
include(":data:calendar")
include(":data:prayer")
include(":data:settings")
include(":data:song")
include(":data:translations")
include(":analytics:firebase-android")
include(":qr")
include(":feature:bible")
include(":feature:calendar")
include(":feature:song")
include(":feature:onboarding-kmp")
include(":core:designsystem")
include(":core:ui-common")
include(":core:app-info")
include(":feature:settings-kmp")
include(":core:platform-kmp")
include(":feature:prayer-kmp")
include(":qr-generation")
