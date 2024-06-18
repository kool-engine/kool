plugins {
    id("kool.lib-conventions")
    id("kool.publish-conventions")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":kool-core"))
            api(project(":kool-physics"))
            api(project(":kool-editor-model"))
            api(libs.kotlin.coroutines)
            api(libs.kotlin.serialization.core)
            api(libs.kotlin.serialization.json)
            api(libs.kotlin.reflect)
            api(libs.kotlin.atomicfu)
            implementation(libs.kotlin.datetime)
        }

        desktopTest.dependencies {
            implementation(fileTree("${projectDir}/../kool-demo/runtimeLibs") { include("*.jar") })
            implementation(libs.jsvg)
        }
    }
}
