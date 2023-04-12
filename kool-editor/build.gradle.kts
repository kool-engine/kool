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

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(DepsCommon.kotlinCoroutines)
                implementation(DepsCommon.kotlinSerialization)
                implementation(DepsCommon.kotlinSerializationJson)
                implementation(project(":kool-core"))
                implementation(project(":kool-physics"))
                implementation(project(":kool-editor-api"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(DepsJvm.lwjgl())
                runtimeOnly(DepsJvm.lwjglNatives())
                runtimeOnly(DepsJvm.lwjglNatives("glfw"))
                runtimeOnly(DepsJvm.lwjglNatives("jemalloc"))
                runtimeOnly(DepsJvm.lwjglNatives("opengl"))
                runtimeOnly(DepsJvm.lwjglNatives("vma"))
                runtimeOnly(DepsJvm.lwjglNatives("shaderc"))
                runtimeOnly(DepsJvm.lwjglNatives("nfd"))
                runtimeOnly(DepsJvm.lwjglNatives("stb"))

                runtimeOnly(DepsJvm.physxJniRuntime)
            }
        }

        sourceSets.all {
            languageSettings.apply {
                progressiveMode = true
                optIn("kotlin.io.path.ExperimentalPathApi")
            }
        }
    }
}

tasks["clean"].doLast {
    delete("${rootDir}/dist/kool-editor")
}