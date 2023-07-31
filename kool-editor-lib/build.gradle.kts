plugins {
    kotlin("plugin.serialization") version Versions.kotlinVersion
}

kotlin {
    jvm {
        jvmToolchain(11)
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
                implementation(DepsCommon.kotlinReflection)
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