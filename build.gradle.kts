plugins {
    kotlin("multiplatform") version Versions.kotlinVersion apply false
    id("org.jetbrains.dokka") version Versions.dokkaVersion apply false
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.22.0")
    }
}

allprojects {
    group = "de.fabmax.kool"
    version = "0.13.0-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
    }

    subprojects {
        apply(plugin = "kotlin-multiplatform")
        apply(plugin = "org.jetbrains.dokka")
        apply(plugin = "kotlinx-atomicfu")
    }
}
