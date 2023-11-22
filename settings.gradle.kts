rootProject.name = "kool"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include("kool-core")
include("kool-physics")
include("kool-editor")
include("kool-demo")
