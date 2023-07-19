@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

kotlin {
    jvm {
        jvmToolchain(11)
    }
    js(IR) {
        binaries.executable()
        browser {
            @OptIn(ExperimentalDistributionDsl::class)
            distribution(Action {
                outputDirectory.set(File("${rootDir}/dist/kool-demo"))
            })
            commonWebpackConfig(Action {
                mode = if (KoolBuildSettings.isRelease) {
                    KotlinWebpackConfig.Mode.PRODUCTION
                } else {
                    KotlinWebpackConfig.Mode.DEVELOPMENT
                }
            })
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(DepsCommon.kotlinCoroutines)
                implementation(DepsCommon.kotlinSerialization)
                implementation(DepsCommon.kotlinSerializationJson)

                // fixme: with intellij kotlin 1.9 plugin, project dependencies seem to have issues
                //  in jvm / js platform sources no kool-core class can be resolved, while in common sources
                //  everything is fine
                //  --> stay on kotlin 1.8 plugin for now (seems to work fine with kotlin 1.9)
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