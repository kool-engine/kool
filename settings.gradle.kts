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
        maven("https://gitlab.com/api/v4/projects/25805863/packages/maven")
        google()
        mavenCentral()
    }
}

include("kool-core")
include("kool-physics")
include("kool-editor-model")
include("kool-editor")
include("kool-demo")
