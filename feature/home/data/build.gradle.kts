plugins {
    id("spotter.library.kmp")
}

kotlin {
    jvm()

    android {
        namespace = "com.example.spotter.feature.home.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "FeatureHomeData"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:data"))
            implementation(project(":core:datastore"))
            implementation(project(":core:domain"))
            implementation(project(":core:model"))
            implementation(project(":core:network"))
            implementation(project(":feature:home:domain"))

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            api(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.play.services.location)
        }
    }
}
