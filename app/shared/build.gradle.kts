import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("spotter.library.compose")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm()

    android {
        namespace = "com.example.spotter.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(project(":core:location"))
            implementation(libs.compose.uiTooling)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
            implementation(libs.kotlinx.coroutinesSwing)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(project(":app:ui-components"))
            implementation(project(":core:model"))
            implementation(project(":core:domain"))
            implementation(project(":core:navigation"))
            implementation(project(":core:network"))
            implementation(project(":core:database"))
            implementation(project(":core:datastore"))
            implementation(project(":feature:home:data"))
            implementation(project(":feature:home:domain"))
            implementation(project(":feature:home:contract"))
            implementation(project(":feature:home:presentation"))
            implementation(project(":feature:map:contract"))
            implementation(project(":feature:map:domain"))
            implementation(project(":feature:map:data"))
            implementation(project(":feature:map:presentation"))
            implementation(project(":feature:favorites:contract"))
            implementation(project(":feature:favorites:domain"))
            implementation(project(":feature:favorites:data"))
            implementation(project(":feature:favorites:presentation"))
            implementation(project(":feature:settings:contract"))
            implementation(project(":feature:settings:presentation"))
            implementation(project(":feature:splash:contract"))
            implementation(project(":feature:splash:presentation"))
            implementation(project(":feature:detail:domain"))
            implementation(project(":feature:detail:presentation"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.ktor.client.core)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.navigation3.ui)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
