plugins {
    id("spotter.library.kmp")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm()

    android {
        namespace = "com.example.spotter.feature.map.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "FeatureMapData"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:data"))
            implementation(project(":core:domain"))
            implementation(project(":core:location"))
            implementation(project(":core:model"))
            implementation(project(":core:network"))
            implementation(project(":feature:home:domain"))
            api(project(":feature:map:domain"))

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            api(libs.koin.core)
        }
    }
}
