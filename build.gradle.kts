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
        mavenCentral()
    }
}
