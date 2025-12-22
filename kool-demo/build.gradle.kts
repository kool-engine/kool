@file:OptIn(ExperimentalWasmDsl::class)

import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    jvm("desktop") {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        binaries {
            executable {
                mainClass.set("de.fabmax.kool.demo.MainKt")
                applicationDefaultJvmArgs = buildList {
                    add("--add-opens=java.base/java.lang=ALL-UNNAMED")
                    add("--enable-native-access=ALL-UNNAMED")
                    if (OperatingSystem.current().isMacOsX) {
                        add("-XstartOnFirstThread")
                    }
                }
                applicationDistribution.duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
        }
    }
    jvmToolchain(25)

    wasmJs {
        outputModuleName = "kool-demo-wasm"
        binaries.executable()
        browser {
            @OptIn(ExperimentalDistributionDsl::class)
            distribution {
                outputDirectory.set(File("${rootDir}/dist/kool-demo-wasm"))
            }
        }
    }

    js {
        binaries.executable()
        browser {
            @OptIn(ExperimentalDistributionDsl::class)
            distribution {
                outputDirectory.set(File("${rootDir}/dist/kool-demo"))
            }
            commonWebpackConfig {
                //KotlinWebpackConfig.Mode.PRODUCTION
                KotlinWebpackConfig.Mode.DEVELOPMENT
            }
        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            target.set("es2015")
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
        freeCompilerArgs.add("-Xcontext-parameters")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":kool-core"))
            implementation(project(":kool-backend-wgpu4k"))
            implementation(project(":kool-physics"))
            implementation(project(":kool-physics-2d"))
            implementation(libs.kotlin.coroutines)
            implementation(libs.kotlin.serialization.core)
            implementation(libs.kotlin.serialization.json)
        }
    }
}

tasks["clean"].dependsOn("deleteExtras")
tasks.register<Delete>("deleteExtras") {
    delete("${rootDir}/dist/kool-demo")
}

tasks.register<JavaExec>("runDesktopSwing") {
    group = "application"
    mainClass.set("de.fabmax.kool.demo.MainSwingKt")
    jvmArgs = listOf(
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--enable-native-access=ALL-UNNAMED"
    )

    kotlin {
        val main = targets["desktop"].compilations["main"]
        dependsOn(main.compileAllTaskName)
        classpath(
            { main.output.allOutputs.files },
            { configurations["desktopRuntimeClasspath"] }
        )
    }
}