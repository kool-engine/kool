rootProject.name = "kool"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include("kool-core")
include("kool-physics")
include("kool-demo")

include("kool-editor")
include("kool-editor-api")
include("kool-editor-template")
