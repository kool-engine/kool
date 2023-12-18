import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(commonLibs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm("desktop") {
        jvmToolchain(11)
    }
    js(IR) {
        binaries.executable()
        browser {
            @OptIn(ExperimentalDistributionDsl::class)
            distribution {
                outputDirectory.set(File("${rootDir}/dist/kool-demo"))
            }
            commonWebpackConfig {
                mode = if (KoolBuildSettings.isRelease) {
                    KotlinWebpackConfig.Mode.PRODUCTION
                } else {
                    KotlinWebpackConfig.Mode.DEVELOPMENT
                }
            }
        }
    }

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(commonLibs.kotlin.coroutines)
            implementation(commonLibs.kotlin.serialization.core)
            implementation(commonLibs.kotlin.serialization.json)

            implementation(project(":kool-core"))
            implementation(project(":kool-physics"))
        }

        desktopMain.dependencies {
            // fixme: force required runtime libraries into IntelliJ module classpath by adding them as implementation
            //  dependencies.
            //  Notice that runtimeLibs are only available and added to classpath after first build (or after
            //  cacheRuntimeLibs task is executed manually) AND the gradle project is re-synced.
            implementation(fileTree("${projectDir}/runtimeLibs") { include("*.jar") })

            // add all native libs potentially needed for running demo as runtimeOnly dependencies, so that they can
            // be found by the cacheRuntimeLibs task
            listOf("natives-linux", "natives-windows", "natives-macos", "natives-macos-arm64").forEach { platform ->
                runtimeOnly("${jvmLibs.lwjgl.core.get()}:$platform")
                runtimeOnly("${jvmLibs.lwjgl.glfw.get()}:$platform")
                runtimeOnly("${jvmLibs.lwjgl.jemalloc.get()}:$platform")
                runtimeOnly("${jvmLibs.lwjgl.nfd.get()}:$platform")
                runtimeOnly("${jvmLibs.lwjgl.opengl.get()}:$platform")
                runtimeOnly("${jvmLibs.lwjgl.shaderc.get()}:$platform")
                runtimeOnly("${jvmLibs.lwjgl.stb.get()}:$platform")
                runtimeOnly("${jvmLibs.lwjgl.vma.get()}:$platform")
                runtimeOnly("${jvmLibs.physxjni.get()}:$platform")
            }
        }
    }

    sourceSets.all {
        languageSettings {
            if (KoolBuildSettings.useK2) {
                languageVersion = "2.0"
            }
        }
    }
}

tasks["build"].dependsOn("cacheRuntimeLibs")
task("cacheRuntimeLibs") {
    doFirst {
        val os = OperatingSystem.current()
        val platformName = when {
            os.isLinux -> "natives-linux"
            os.isWindows -> "natives-windows"
            os.isMacOsX && "arm" in os.nativePrefix -> "natives-macos-arm64"
            os.isMacOsX -> "natives-macos"
            else -> ""
        }

        configurations
            .filter { it.name == "desktopRuntimeClasspath" }
            .flatMap { it.copyRecursive().fileCollection { true } }
            .filter { it.name.endsWith("$platformName.jar") && !it.path.startsWith(projectDir.path) }
            .forEach {
                if (!File("${projectDir}/runtimeLibs/${it.name}").exists()) {
                    println("copy: $it")
                    copy {
                        from(it)
                        into("${projectDir}/runtimeLibs")
                    }
                }
            }
    }
}

tasks["clean"].doLast {
    delete("${rootDir}/dist/kool-demo")
    delete("${projectDir}/runtimeLibs")
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