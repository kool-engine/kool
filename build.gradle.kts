plugins {
    kotlin("multiplatform") version Versions.kotlinVersion apply false
}

allprojects {
    group = "de.fabmax.kool"
    version = "0.8.0"

    repositories {
        mavenCentral()
        mavenLocal()
    }

    subprojects {
        apply(plugin = "kotlin-multiplatform")
    }
}
