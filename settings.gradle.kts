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
        mavenCentral()
    }
}

include("kool-core")
include("kool-backend-wgpu4k")
include("kool-physics")
include("kool-editor-model")
include("kool-editor")
include("kool-demo")
