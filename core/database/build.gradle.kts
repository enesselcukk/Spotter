plugins {
    id("spotter.library.kmp")
    alias(libs.plugins.ksp)
    alias(libs.plugins.room3)
}

kotlin {
    jvm()

    android {
        namespace = "com.example.spotter.core.database"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        androidResources {
            enable = true
        }
        withHostTest {}
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "CoreDatabase"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:model"))
            implementation(libs.room3.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        getByName("androidHostTest") {
            dependencies {
                implementation(libs.sqlite.bundled.jvm)
            }
        }
    }
}

room3 {
    schemaDirectory(layout.projectDirectory.dir("schemas").asFile.path)
}

dependencies {
    add("kspAndroid", libs.room3.compiler)
    add("kspJvm", libs.room3.compiler)
    add("kspIosArm64", libs.room3.compiler)
    add("kspIosSimulatorArm64", libs.room3.compiler)
}
