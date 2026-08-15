plugins {
    id("spotter.library.kmp")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm()

    android {
        namespace = "com.example.spotter.feature.home.domain"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":core:datastore"))
            api(project(":core:model"))
            implementation(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.serialization.json)

            api(libs.koin.core)
        }
    }
}
