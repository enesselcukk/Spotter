plugins {
    id("spotter.library.kmp")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm()

    android {
        namespace = "com.example.spotter.feature.settings.contract"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core:navigation"))
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
