plugins {
    id("spotter.library.compose")
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.example.spotter.feature.splash.presentation.generated.resources"
}

kotlin {
    jvm()

    android {
        namespace = "com.example.spotter.feature.splash.presentation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        androidResources {
            enable = true
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "FeatureSplash"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:presentation"))
            implementation(project(":core:navigation"))
            implementation(project(":feature:splash:contract"))
            implementation(project(":feature:home:domain"))
            implementation(project(":app:ui-components"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            implementation(libs.koin.core.viewmodel)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}
