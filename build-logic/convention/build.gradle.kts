plugins {
    `kotlin-dsl`
}

group = "com.example.spotter.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.composeCompiler.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kmpLibrary") {
            id = "spotter.library.kmp"
            implementationClass = "KmpLibraryConventionPlugin"
        }
        register("kmpCompose") {
            id = "spotter.library.compose"
            implementationClass = "KmpComposeConventionPlugin"
        }
        register("androidApplication") {
            id = "spotter.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
    }
}
