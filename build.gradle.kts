plugins {
    alias(commonLibs.plugins.androidApplication) apply false
    alias(commonLibs.plugins.androidLibrary) apply false
    alias(commonLibs.plugins.kotlinMultiplatform) apply false
    alias(commonLibs.plugins.kotlinSerialization) apply false
    alias(commonLibs.plugins.kotlinDokka) apply false
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${commonLibs.versions.kotlin.atomicfu.get()}")
    }
}

allprojects {
    group = "de.fabmax.kool"
    version = "0.14.0-SNAPSHOT"

    subprojects {
        apply(plugin = "kotlinx-atomicfu")
    }
}
