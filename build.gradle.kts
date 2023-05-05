plugins {
    kotlin("multiplatform") version Versions.kotlinVersion apply false
    id("org.jetbrains.dokka") version Versions.dokkaVersion apply false
}

allprojects {
    group = "de.fabmax.kool"
    version = "0.12.0-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
    }

    subprojects {
        apply(plugin = "kotlin-multiplatform")
        apply(plugin = "org.jetbrains.dokka")
    }
}
