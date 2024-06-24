import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
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
                mode = if (LocalProperties.get(project).isRelease) {
                    KotlinWebpackConfig.Mode.PRODUCTION
                } else {
                    KotlinWebpackConfig.Mode.DEVELOPMENT
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":kool-core"))
            implementation(project(":kool-physics"))
            implementation(libs.kotlin.coroutines)
            implementation(libs.kotlin.serialization.core)
            implementation(libs.kotlin.serialization.json)
        }

        val desktopMain by getting
        desktopMain.dependencies {
            // fixme: force required runtime libraries into IntelliJ module classpath by adding them as implementation
            //  dependencies.
            //  Notice that runtimeLibs are only available and added to classpath after first build (or after
            //  cacheRuntimeLibs task is executed manually) AND the gradle project is re-synced.
            implementation(fileTree("${projectDir}/runtimeLibs") { include("*.jar") })
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