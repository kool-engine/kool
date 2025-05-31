import de.fabmax.kool.comment

plugins {
    id("kool.androidlib-conventions") apply false
    id("kool.lib-conventions") apply false
    id("kool.publish-conventions") apply false
}

allprojects {
    group = "de.fabmax.kool"
    version = "0.18.0-SNAPSHOT"

}

tasks.register("disableAndroidPlatform") {
    group = "build config"
    doFirst {
        listOf("kool-core", "kool-editor-model").forEach { subProj ->
            project.file("${subProj}/build.gradle.kts").comment {
                commentLines("alias(libs.plugins.androidLibrary)")
                commentBlocks("android")
            }
        }
        rootProject.file("buildSrc/src/main/kotlin/kool.androidlib-conventions.gradle.kts").comment {
            commentLines("id(\"com.android.library\")")
            commentBlocks("android")
        }
    }
}

tasks.register("enableAndroidPlatform") {
    group = "build config"
    doFirst {
        listOf("kool-core", "kool-editor-model").forEach { subProj ->
            project.file("$subProj/build.gradle.kts").comment {
                uncommentLines("alias(libs.plugins.androidLibrary)")
                uncommentBlocks("android")
            }
        }
        rootProject.file("buildSrc/src/main/kotlin/kool.androidlib-conventions.gradle.kts").comment {
            uncommentLines("id(\"com.android.library\")")
            uncommentBlocks("android")
        }
    }
}
