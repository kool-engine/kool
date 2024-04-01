import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm("desktop") { }
    jvmToolchain(11)

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
            implementation(libs.kotlin.coroutines)
            implementation(libs.kotlin.serialization.core)
            implementation(libs.kotlin.serialization.json)

            implementation(project(":kool-core"))
            implementation(project(":kool-physics"))
        }

        desktopMain.dependencies {
            // fixme: force required runtime libraries into IntelliJ module classpath by adding them as implementation
            //  dependencies.
            //  Notice that runtimeLibs are only available and added to classpath after first build (or after
            //  cacheRuntimeLibs task is executed manually) AND the gradle project is re-synced.
            implementation(fileTree("${projectDir}/runtimeLibs") { include("*.jar") })
            implementation(libs.jsvg)

            // add all native libs potentially needed for running demo as runtimeOnly dependencies, so that they can
            // be found by the cacheRuntimeLibs task
            listOf("natives-linux", "natives-windows", "natives-macos", "natives-macos-arm64").forEach { platform ->
                runtimeOnly("${libs.lwjgl.core.get()}:$platform")
                runtimeOnly("${libs.lwjgl.glfw.get()}:$platform")
                runtimeOnly("${libs.lwjgl.jemalloc.get()}:$platform")
                runtimeOnly("${libs.lwjgl.nfd.get()}:$platform")
                runtimeOnly("${libs.lwjgl.opengl.get()}:$platform")
                runtimeOnly("${libs.lwjgl.shaderc.get()}:$platform")
                runtimeOnly("${libs.lwjgl.stb.get()}:$platform")
                runtimeOnly("${libs.lwjgl.vma.get()}:$platform")
                runtimeOnly("${libs.physxjni.get()}:$platform")
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

        val runtimeLibs = configurations
            .filter { it.name == "desktopRuntimeClasspath" }
            .flatMap { it.copyRecursive().fileCollection { true } }
            .filter { it.name.endsWith("$platformName.jar") && !it.path.startsWith(projectDir.path) }
        runtimeLibs
            .forEach {
                if (!File("${projectDir}/runtimeLibs/${it.name}").exists()) {
                    copy {
                        from(it)
                        into("${projectDir}/runtimeLibs")
                    }
                }
            }
        File("${projectDir}/runtimeLibs/").listFiles()
            ?.filter { exiting -> runtimeLibs.none { exiting.name == it.name } }
            ?.forEach { it.delete() }
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
        val main = targets["desktop"].compilations["main"]
        dependsOn(main.compileAllTaskName)
        classpath(
            { main.output.allOutputs.files },
            { configurations["desktopRuntimeClasspath"] }
        )
    }
}