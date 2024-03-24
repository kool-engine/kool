plugins {
    alias(commonLibs.plugins.androidApplication) apply false
    alias(commonLibs.plugins.androidLibrary) apply false
    alias(commonLibs.plugins.kotlinMultiplatform) apply false
    alias(commonLibs.plugins.kotlinSerialization) apply false
    alias(commonLibs.plugins.kotlinAtomicFu) apply false
    alias(commonLibs.plugins.kotlinDokka) apply false
}

allprojects {
    group = "de.fabmax.kool"
    version = "0.14.0"

    repositories {
        mavenCentral()
    }
}
