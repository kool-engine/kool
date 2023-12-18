rootProject.name = "kool"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenLocal()
        mavenCentral()
    }

    versionCatalogs {
        create("commonLibs") {
            version("agp", "8.1.4")
            version("android-compileSdk", "33")
            version("android-minSdk", "24")
            version("kotlin", "1.9.21")
            version("kotlin-coroutines", "1.7.3")
            version("kotlin-serialization", "1.6.1")
            version("kotlin-atomicfu", "0.23.1")
            version("kotlin-dokka", "1.9.10")

            library("kotlin-coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef("kotlin-coroutines")
            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").versionRef("kotlin")
            library("kotlin-serialization-core", "org.jetbrains.kotlinx", "kotlinx-serialization-core").versionRef("kotlin-serialization")
            library("kotlin-serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").versionRef("kotlin-serialization")
            library("kotlin-test", "org.jetbrains.kotlin", "kotlin-test").versionRef("kotlin")
            library("kotlin-test-junit", "org.jetbrains.kotlin", "kotlin-test-junit").versionRef("kotlin")

            plugin("androidApplication", "com.android.application").versionRef("agp")
            plugin("androidLibrary", "com.android.library").versionRef("agp")
            plugin("kotlinSerialization", "org.jetbrains.kotlin.plugin.serialization").versionRef("kotlin")
            plugin("kotlinMultiplatform", "org.jetbrains.kotlin.multiplatform").versionRef("kotlin")
            plugin("kotlinDokka", "org.jetbrains.dokka").versionRef("kotlin-dokka")
        }

        create("jvmLibs") {
            version("lwjgl", "3.3.3")

            library("jsvg", "com.github.weisj:jsvg:1.3.0")
            library("lwjgl-core", "org.lwjgl", "lwjgl").versionRef("lwjgl")
            library("lwjgl-glfw", "org.lwjgl", "lwjgl-glfw").versionRef("lwjgl")
            library("lwjgl-jemalloc", "org.lwjgl", "lwjgl-jemalloc").versionRef("lwjgl")
            library("lwjgl-opengl", "org.lwjgl", "lwjgl-opengl").versionRef("lwjgl")
            library("lwjgl-vulkan", "org.lwjgl", "lwjgl-vulkan").versionRef("lwjgl")
            library("lwjgl-vma", "org.lwjgl", "lwjgl-vma").versionRef("lwjgl")
            library("lwjgl-shaderc", "org.lwjgl", "lwjgl-shaderc").versionRef("lwjgl")
            library("lwjgl-nfd", "org.lwjgl", "lwjgl-nfd").versionRef("lwjgl")
            library("lwjgl-stb", "org.lwjgl", "lwjgl-stb").versionRef("lwjgl")
            library("physxjni", "de.fabmax:physx-jni:2.3.1")
        }
    }
}

include("kool-core")
include("kool-physics")
include("kool-editor")
include("kool-demo")
