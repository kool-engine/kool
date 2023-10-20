plugins {
    kotlin("multiplatform") version Versions.kotlinVersion apply false
    id("org.jetbrains.dokka") version Versions.dokkaVersion apply false
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.21.0")
    }
}

allprojects {
    group = "de.fabmax.kool"
    version = "0.12.1"

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
