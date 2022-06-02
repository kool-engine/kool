plugins {
    kotlin("multiplatform") version Versions.kotlinVersion apply false
}

allprojects {
    group = "de.fabmax.kool"
    version = "0.9.1-SNAPSHOT"

    repositories {
        mavenCentral()
        mavenLocal()
    }

    subprojects {
        apply(plugin = "kotlin-multiplatform")
    }
}
