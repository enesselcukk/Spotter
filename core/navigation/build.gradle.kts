plugins {
    id("spotter.library.compose")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm()

    android {
        namespace = "com.example.spotter.core.navigation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "CoreNavigation"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":app:ui-components"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            api(libs.koin.core)
        }
    }
}
