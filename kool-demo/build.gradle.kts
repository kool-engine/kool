import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    jvm("desktop") {
//        @OptIn(ExperimentalKotlinGradlePluginApi::class)
//        binaries {
//            // build fails for duplicate libraries, despite duplicate strategy being EXCLUDE
//            executable {
//                mainClass.set("de.fabmax.kool.demo.MainKt")
//                if (OperatingSystem.current().isMacOsX) {
//                    applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
//                }
//                applicationDistribution.duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//            }
//        }
    }

    js {
        binaries.executable()
        browser {
            @OptIn(ExperimentalDistributionDsl::class)
            distribution {
                outputDirectory.set(File("${rootDir}/dist/kool-demo"))
            }
            commonWebpackConfig {
                mode = if (localProperties.isRelease) {
                    KotlinWebpackConfig.Mode.PRODUCTION
                } else {
                    KotlinWebpackConfig.Mode.DEVELOPMENT
                }
            }
        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            target.set("es2015")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":kool-core"))
            implementation(project(":kool-backend-wgpu4k"))
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
tasks.register("cacheRuntimeLibs") {
    doFirst {
        val os = OperatingSystem.current()
        val platformName = when {
            os.isLinux -> "natives-linux"
            os.isWindows -> "natives-windows"
            os.isMacOsX && "aarch64" in System.getProperty("os.arch") -> "natives-macos-arm64"
            os.isMacOsX -> "natives-macos"
            else -> ""
        }

        val runtimeLibs = configurations
            .filter { it.name == "desktopRuntimeClasspath" }
            .flatMap { it.files.toList() }
            .filter { it.name.endsWith("$platformName.jar") && !it.path.startsWith(projectDir.path) }
            .onEach {
                if (!File("${projectDir}/runtimeLibs/${it.name}").exists()) {
                    copy {
                        from(it)
                        into("${projectDir}/runtimeLibs")
                    }
                }
            }
        File("${projectDir}/runtimeLibs/").listFiles()
            ?.filter { existing -> runtimeLibs.none { existing.name == it.name } }
            ?.forEach { it.delete() }
    }
}

tasks["clean"].doLast {
    delete("${rootDir}/dist/kool-demo")
    delete("${projectDir}/runtimeLibs")
}

tasks.register<JavaExec>("runDesktop") {
    dependsOn("cacheRuntimeLibs")
    group = "application"
    mainClass.set("de.fabmax.kool.demo.MainKt")

    var customJvmArgs = listOf(
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--enable-native-access=ALL-UNNAMED"
    )

    if (OperatingSystem.current().isMacOsX) {
        customJvmArgs += listOf("-XstartOnFirstThread")
    }
    jvmArgs = customJvmArgs

    kotlin {
        val main = targets["desktop"].compilations["main"]
        dependsOn(main.compileAllTaskName)
        classpath(
            { main.output.allOutputs.files },
            { configurations["desktopRuntimeClasspath"] }
        )
    }
}