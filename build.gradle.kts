plugins {
    kotlin("multiplatform") version Versions.kotlin apply false
    id("org.jetbrains.dokka") version Versions.dokka apply false
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${Versions.atomicfu}")
    }
}

allprojects {
    group = "de.fabmax.kool"
    version = "0.13.0"

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
