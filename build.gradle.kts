plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.kotlinAtomicFu) apply false
    alias(libs.plugins.kotlinDokka) apply false
}

allprojects {
    group = "de.fabmax.kool"
    version = "0.15.0-SNAPSHOT"

    repositories {
        google()
        mavenCentral()
    }
}

task("disableAndroidPlatform") {
    group = "build config"
    doFirst {
        File(projectDir, "kool-core/build.gradle.kts").comment {
            commentLines("alias(libs.plugins.androidLibrary)")
            commentBlocks("android")
        }
    }
}

task("enableAndroidPlatform") {
    group = "build config"
    doFirst {
        File(projectDir, "kool-core/build.gradle.kts").comment {
            uncommentLines("alias(libs.plugins.androidLibrary)")
            uncommentBlocks("android")
        }
    }
}
