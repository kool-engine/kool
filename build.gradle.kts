plugins {
    kotlin("multiplatform") version Versions.kotlinVersion apply false
}

allprojects {
    group = "de.fabmax.kool"
    version = "0.9.3-SNAPSHOT"

    repositories {
        mavenCentral()
        mavenLocal()
    }

    subprojects {
        apply(plugin = "kotlin-multiplatform")
    }
}
