@file:Suppress("UNUSED_VARIABLE")

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