@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

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
        browser {
            distribution {
                directory = File("${rootDir}/dist/kool-demo")
            }
            commonWebpackConfig {
                // small js code
                mode = KotlinWebpackConfig.Mode.PRODUCTION
                // readable js code but ~twice the file size
                //mode = KotlinWebpackConfig.Mode.DEVELOPMENT
            }
            binaries.executable()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
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
                runtimeOnly(DepsJvm.lwjglNatives())
                runtimeOnly(DepsJvm.lwjglNatives("glfw"))
                runtimeOnly(DepsJvm.lwjglNatives("jemalloc"))
                runtimeOnly(DepsJvm.lwjglNatives("opengl"))
                runtimeOnly(DepsJvm.lwjglNatives("vma"))
                runtimeOnly(DepsJvm.lwjglNatives("shaderc"))
            }
        }

        val jsMain by getting {
            dependencies { }
        }

        sourceSets.all {
            languageSettings.apply {
                progressiveMode = true
                useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            }
        }
    }
}

tasks["clean"].doLast {
    delete("${rootDir}/dist/kool-demo")
}