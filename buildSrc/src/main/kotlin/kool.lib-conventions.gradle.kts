@file:OptIn(ExperimentalKotlinGradlePluginApi::class, ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.atomicfu")
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlin.plugin.js-plain-objects")
}

kotlin {
    jvm("desktop") { }
    jvmToolchain(25)
    js {
        binaries.library()
        browser()
        compilerOptions {
            target.set("es2015")
        }
    }
    wasmJs {
        binaries.library()
        browser()
    }

    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
        freeCompilerArgs.add("-Xcontext-parameters")
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets.all {
        languageSettings {
            optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            optIn("kotlin.contracts.ExperimentalContracts")
            optIn("kotlin.io.encoding.ExperimentalEncodingApi")
            optIn("kotlin.ExperimentalStdlibApi")
        }
    }
    sourceSets {
        jsMain {
            languageSettings {
                optIn("kotlin.js.ExperimentalWasmJsInterop")
            }
        }
        webMain {
            languageSettings {
                optIn("kotlin.js.ExperimentalWasmJsInterop")
            }
        }
        wasmJsMain {
            languageSettings {
                optIn("kotlin.js.ExperimentalWasmJsInterop")
            }
        }
    }
}

dokka {
    dokkaGeneratorIsolation = ClassLoaderIsolation()
}