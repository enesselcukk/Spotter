plugins {
    id("spotter.library.compose")
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.example.spotter.feature.home.presentation.generated.resources"
}

kotlin {
    jvm()

    android {
        namespace = "com.example.spotter.feature.home.presentation"
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
            baseName = "FeatureHome"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":core:presentation"))
            implementation(project(":core:datastore"))
            implementation(project(":core:navigation"))
            implementation(project(":core:spot-ui"))
            implementation(project(":feature:home:contract"))
            implementation(project(":feature:detail:contract"))
            implementation(project(":feature:home:domain"))
            implementation(project(":feature:map:contract"))
            implementation(project(":feature:map:domain"))
            implementation(project(":feature:map:presentation"))
            implementation(project(":feature:favorites:contract"))
            implementation(project(":feature:favorites:domain"))
            implementation(project(":feature:settings:contract"))
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
            implementation(libs.coil.compose)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.activity.compose)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
            implementation(libs.kotlinx.coroutinesSwing)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
