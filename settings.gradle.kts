rootProject.name = "kool"

pluginManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        //mavenLocal()
        maven { url = uri("https://central.sonatype.com/repository/maven-snapshots") }
        google()
        mavenCentral()
    }
}

include("kool-core")
include("kool-backend-wgpu4k")
include("kool-physics")
include("kool-physics-2d")
include("kool-editor-model")
include("kool-editor")
include("kool-demo")
include("kool-compose-ui")
