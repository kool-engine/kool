@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("plugin.serialization") version Versions.kotlinVersion
    `maven-publish`
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
                useIR = true
            }
        }
    }
    //js(IR) { // 1.4.20 build succeeds, but webapp crashes
    js {
        browser { }
    }
    targets.all {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(DepsCommon.kotlinCoroutines)
                implementation(DepsCommon.kotlinSerialization)
                implementation(DepsCommon.kotlinSerializationJson)
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
                implementation(kotlin("reflect"))

                implementation(DepsJvm.jTransforms)
                implementation(DepsJvm.lwjgl())
                implementation(DepsJvm.lwjgl("glfw"))
                implementation(DepsJvm.lwjgl("jemalloc"))
                implementation(DepsJvm.lwjgl("opengl"))
                implementation(DepsJvm.lwjgl("vulkan"))
                implementation(DepsJvm.lwjgl("vma"))
                implementation(DepsJvm.lwjgl("shaderc"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(npm("pako", "1.0.11"))
            }
        }

        sourceSets.all {
            languageSettings.apply {
                progressiveMode = true
                useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
                useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
                useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
                useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            }
        }
    }
}

tasks.register("updateVersion") {
    val koolContextSrcFile = kotlin.sourceSets.findByName("commonMain")?.kotlin
            ?.sourceDirectories
            ?.map { File(it, "de/fabmax/kool/KoolContext.kt") }
            ?.find { it.exists() }
    koolContextSrcFile?.let { updateVersionCode(it, version) }
}
tasks["compileKotlinJs"].dependsOn("updateVersion")
tasks["compileKotlinJvm"].dependsOn("updateVersion")

val publishCredentials = PublishingCredentials("$rootDir/publishingCredentials.properties")
if (publishCredentials.isAvailable) {
    publishing {
        repositories {
            maven {
                url = uri("${publishCredentials.repoUrl}/kool-core")
                credentials {
                    username = publishCredentials.username
                    password = publishCredentials.password
                }
            }
        }

        publications {
            publications.filterIsInstance<MavenPublication>().forEach {
                if (it.name == "kotlinMultiplatform") {
                    // this is the publication for the common project, which only contains metadata referring to
                    // the platform-projects. However, we need to add a jar to make the repo happy
                    it.artifact(tasks["jvmSourcesJar"])
                }

                it.pom {
                    name.set("kool")
                    description.set("A multiplatform OpenGL / Vulkan graphics engine written in kotlin")
                    url.set("https://github.com/fabmax/kool")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    scm {
                        connection.set("scm:git:https://github.com/fabmax/kool.git")
                        developerConnection.set("scm:git:https://github.com/fabmax/kool.git")
                        url.set("https://github.com/fabmax/kool")
                    }
                }
            }
        }
    }
}