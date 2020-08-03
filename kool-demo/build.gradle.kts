kotlin {
    targets {
        jvm {
            compilations.all {
                kotlinOptions {
                    jvmTarget = "1.8"
                }
            }
        }
        js {
            val main by compilations.getting {
                kotlinOptions {
                    outputFile = "${buildDir}/web/kooldemo.js"
                    moduleKind = "amd"
                    sourceMap = false
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(DepsCommon.kotlinCoroutines)
                implementation(DepsCommon.kotlinSerialization)
                implementation(project(":kool-core"))
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
                implementation(kotlin("stdlib-jdk8"))
                implementation(DepsJvm.kotlinCoroutines)
                implementation(DepsJvm.kotlinSerialization)

                runtimeOnly(DepsJvm.lwjglNatives())
                runtimeOnly(DepsJvm.lwjglNatives("glfw"))
                runtimeOnly(DepsJvm.lwjglNatives("jemalloc"))
                runtimeOnly(DepsJvm.lwjglNatives("opengl"))
                runtimeOnly(DepsJvm.lwjglNatives("vma"))
                runtimeOnly(DepsJvm.lwjglNatives("shaderc"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(DepsJs.kotlinCoroutines)
                implementation(DepsJs.kotlinSerialization)
            }
        }

        sourceSets.all {
            languageSettings.apply {
                progressiveMode = true
                useExperimentalAnnotation("kotlinx.serialization.ImplicitReflectionSerializer")
                useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            }
        }
    }
}

tasks["clean"].doLast {
    delete("${rootDir}/docs/kool-js")
}

tasks["build"].doLast {
    // deploy js demo including all required runtime dependencies into 'docs/kool-js' folder
    val deployPath = "${rootDir}/docs/kool-js"

    configurations.findByName("jsRuntimeClasspath")?.forEach { file ->
        copy {
            includeEmptyDirs = false
            from(zipTree(file.absolutePath))
            into(deployPath)
            include { fileTreeElement ->
                val path = fileTreeElement.path
                path.endsWith(".js") && (path.startsWith("META-INF/resources/") || !path.startsWith("META-INF/"))
            }
        }
    }

    copy {
        includeEmptyDirs = false
        from("${buildDir}/web")
        into(deployPath)
        include { fileTreeElement ->
            fileTreeElement.path.endsWith(".js")
        }
    }

    copy {
        from(kotlin.sourceSets.findByName("jsMain")!!.resources.srcDirs)
        into(deployPath)
    }
}