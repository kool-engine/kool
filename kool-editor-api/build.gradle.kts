@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("plugin.serialization") version Versions.kotlinVersion
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = freeCompilerArgs + "-Xbackend-threads=0"
            }
        }
    }
    js(IR) {
        binaries.library()
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(DepsCommon.kotlinCoroutines)
                implementation(DepsCommon.kotlinSerialization)
                implementation(DepsCommon.kotlinSerializationJson)
                api(project(":kool-core"))
                api(project(":kool-physics"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies { }
        }

        val jsMain by getting {
            dependencies { }
        }

        sourceSets.all {
            languageSettings.apply {
                progressiveMode = true
            }
        }
    }
}