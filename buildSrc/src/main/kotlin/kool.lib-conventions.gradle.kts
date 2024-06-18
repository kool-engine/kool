@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.atomicfu")
    id("org.jetbrains.dokka")
}

kotlin {
    jvm("desktop") { }
    jvmToolchain(11)
    js(IR) {
        binaries.library()
        browser()
    }

    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
        freeCompilerArgs.add("-Xcontext-receivers")
    }

    sourceSets.all {
        languageSettings {
            optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            optIn("kotlin.contracts.ExperimentalContracts")
            optIn("kotlin.io.encoding.ExperimentalEncodingApi")
            optIn("kotlin.ExperimentalStdlibApi")
        }
    }
}