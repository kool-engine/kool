@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.atomicfu")
    id("org.jetbrains.dokka")
}

kotlin {
    jvm("desktop") { }
    jvmToolchain(22)
    js {
        binaries.library()
        browser()
        compilerOptions {
            target.set("es2015")
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
        freeCompilerArgs.add("-Xcontext-receivers")
        freeCompilerArgs.add("-Xwhen-guards")
    }

    sourceSets.all {
        languageSettings {
            optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            optIn("kotlin.contracts.ExperimentalContracts")
            optIn("kotlin.io.encoding.ExperimentalEncodingApi")
            optIn("kotlin.ExperimentalStdlibApi")
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

dokka {
    dokkaGeneratorIsolation = ClassLoaderIsolation()
}