kotlin {
    jvm {
        jvmToolchain(11)
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(DepsCommon.kotlinCoroutines)
                api(DepsCommon.kotlinSerialization)
                api(DepsCommon.kotlinSerializationJson)
                api(DepsCommon.kotlinReflection)
                api(project(":kool-core"))
                api(project(":kool-physics"))
                api(project(":kool-editor-lib"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
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