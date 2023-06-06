@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

kotlin {
    jvm {
        jvmToolchain(11)
    }

    jvm("editor") {
        jvmToolchain(11)
    }

    js(IR) {
        binaries.executable()
        browser {
            @OptIn(ExperimentalDistributionDsl::class)
            distribution {
                directory = File("${projectDir}/jsDist")
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
        val commonMain by getting {
            dependencies {
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

        val editorMain by getting {
            dependencies {
                implementation(project(":kool-editor"))
            }
        }

        sourceSets.all {
            languageSettings.apply {
                progressiveMode = true
            }
        }
    }
}

configurations.filter { "editor" in it.name }.forEach {
    // editor related configurations need some custom attribute to distinguish them from regular jvm configs
    it.attributes.attribute(Attribute.of("de.fabmax.kool-editor", String::class.java), "editor")
}

tasks["clean"].doLast {
    delete("${projectDir}/jsDist")
}

task("runEditor", JavaExec::class) {
    group = "editor"
    dependsOn("editorMainClasses")

    val editorConfig = configurations.getByName("editorRuntimeClasspath").copyRecursive()

    classpath = editorConfig.fileCollection { true } + files("$buildDir/classes/kotlin/editor/main")
    mainClass.set("EditorLauncherKt")
    workingDir = File(projectDir, ".editor")

    if (!workingDir.exists()) {
        workingDir.mkdir()
    }
}
