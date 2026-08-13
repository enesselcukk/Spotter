plugins {
    id("spotter.library.compose")
}

kotlin {
    jvm()

    android {
        namespace = "com.example.spotter.feature.detail.presentation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "FeatureDetail"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:detail:contract"))
            implementation(project(":feature:detail:domain"))
            implementation(project(":app:ui-components"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
