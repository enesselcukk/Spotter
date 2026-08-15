rootProject.name = "Spotter"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":composeApp")
include(":iosApp")
include(":app:shared")
include(":app:ui-components")
include(":core:model")
include(":core:domain")
include(":core:network")
include(":core:database")
include(":core:data")
include(":core:datastore")
include(":core:navigation")
include(":core:spot-ui")
include(":feature:home:contract")
include(":feature:home:domain")
include(":feature:home:data")
include(":feature:home:presentation")
include(":feature:map:contract")
include(":feature:map:domain")
include(":feature:map:data")
include(":feature:map:presentation")
include(":feature:favorites:contract")
include(":feature:favorites:domain")
include(":feature:favorites:data")
include(":feature:favorites:presentation")
include(":feature:settings:contract")
include(":feature:settings:presentation")
include(":feature:splash:contract")
include(":feature:splash:presentation")
include(":feature:detail:contract")
include(":feature:detail:domain")
include(":feature:detail:data")
include(":feature:detail:presentation")

include(":core:presentation")
