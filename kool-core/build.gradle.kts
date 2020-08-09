plugins {
    kotlin("plugin.serialization") version Versions.kotlinVersion
    `maven-publish`
}

kotlin {
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
                outputFile = "${buildDir}/web/kool.js"
                moduleKind = "amd"
                sourceMap = false
            }
        }
    }
    targets.all {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(DepsCommon.kotlinCoroutines)
                implementation(DepsCommon.kotlinSerialization)
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
                implementation(kotlin("reflect"))
                implementation(DepsJvm.kotlinCoroutines)
                implementation(DepsJvm.kotlinSerialization)

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
                implementation(kotlin("stdlib-js"))
                implementation(DepsJs.kotlinCoroutines)
                implementation(DepsJs.kotlinSerialization)
            }
        }

        sourceSets.all {
            languageSettings.apply {
                progressiveMode = true
                useExperimentalAnnotation("kotlinx.serialization.ImplicitReflectionSerializer")
                useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
                useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
                useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
                useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            }
        }
    }
}

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