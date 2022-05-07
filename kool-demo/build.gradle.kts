@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

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
        browser {
            @Suppress("OPT_IN_IS_NOT_ENABLED")
            @OptIn(ExperimentalDistributionDsl::class)
            distribution {
                directory = File("${rootDir}/dist/kool-demo")
            }
            commonWebpackConfig {
                mode = if (KoolBuildSettings.isRelease) {
                    KotlinWebpackConfig.Mode.PRODUCTION
                } else {
                    KotlinWebpackConfig.Mode.DEVELOPMENT
                }
            }
            binaries.executable()
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
                implementation(DepsJvm.lwjgl("stb"))
                runtimeOnly(DepsJvm.lwjglNatives("stb"))

                runtimeOnly(DepsJvm.lwjglNatives())
                runtimeOnly(DepsJvm.lwjglNatives("glfw"))
                runtimeOnly(DepsJvm.lwjglNatives("jemalloc"))
                runtimeOnly(DepsJvm.lwjglNatives("opengl"))
                runtimeOnly(DepsJvm.lwjglNatives("vma"))
                runtimeOnly(DepsJvm.lwjglNatives("shaderc"))
                runtimeOnly(DepsJvm.lwjglNatives("nfd"))

                implementation("de.fabmax:physx-jni:1.0.0")
                runtimeOnly("de.fabmax:physx-jni:1.0.0:natives-windows")
                runtimeOnly("de.fabmax:physx-jni:1.0.0:natives-linux")
                runtimeOnly("de.fabmax:physx-jni:1.0.0:natives-macos")
            }
        }

        val jsMain by getting {
            dependencies { }
        }

        sourceSets.all {
            languageSettings.apply {
                progressiveMode = true
                optIn("kotlin.ExperimentalStdlibApi")
            }
        }
    }
}

tasks["clean"].doLast {
    delete("${rootDir}/dist/kool-demo")
}

tasks.register<JavaExec>("run") {
    group = "application"
    mainClass.set("de.fabmax.kool.demo.MainKt")

    kotlin {
        val main = targets["jvm"].compilations["main"]
        dependsOn(main.compileAllTaskName)
        classpath(
            { main.output.allOutputs.files },
            { configurations["jvmRuntimeClasspath"] }
        )
    }
}