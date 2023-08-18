import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

kotlin {
    jvm {
        jvmToolchain(11)
        compilations.create("editor")
    }

    js(IR) {
        binaries.executable()
        browser {
            @OptIn(ExperimentalDistributionDsl::class)
            distribution(Action {
                outputDirectory.set(File("${projectDir}/jsDist"))
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
                implementation(project(":kool-core"))
                implementation(project(":kool-physics"))
                implementation(project(":kool-editor-lib"))
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

        val jvmEditor by getting {
            dependencies {
                implementation(project(":kool-editor"))
            }
        }

        val jsMain by getting {
            dependencies {
                // fixme: editor dependency should only be included if js editor project is build
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

tasks["jsBrowserDistribution"].doLast {
    // collect assets required to run the editor from various project resource dirs (fonts, icons, etc.)
    copy {
        from("$rootDir/kool-core/src/jvmMain/resources/")
        into("${projectDir}/jsDist")
    }
    copy {
        from("$rootDir/kool-editor/src/commonMain/resources/")
        into("${projectDir}/jsDist")
    }
    copy {
        from("$rootDir/kool-editor/src/jsMain/resources/")
        into("${projectDir}/jsDist")
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
    dependsOn("jvmEditorClasses")

    val editorConfig = configurations.getByName("jvmEditorRuntimeClasspath").copyRecursive()

    classpath = editorConfig.fileCollection { true } + files("$buildDir/classes/kotlin/jvm/editor")
    mainClass.set("EditorLauncherKt")
    workingDir = File(projectDir, ".editor")

    if (!workingDir.exists()) {
        workingDir.mkdir()
    }
}
