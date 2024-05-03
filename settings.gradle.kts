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
}

include("kool-core")
include("kool-physics")
include("kool-editor-model")
include("kool-editor")
include("kool-demo")
