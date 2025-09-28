import de.fabmax.kool.UnCommentTask

plugins {
    id("kool.androidlib-conventions") apply false
    id("kool.lib-conventions") apply false
    id("kool.publish-conventions") apply false
}

allprojects {
    group = "de.fabmax.kool"
    version = "0.19.0-SNAPSHOT"
}

tasks.register<UnCommentTask>("disableAndroidPlatform") {
    group = "build config"

    filesToUpdate += rootProject.file("buildSrc/src/main/kotlin/kool.androidlib-conventions.gradle.kts")
    listOf("kool-core", "kool-editor-model").forEach { subProj ->
        filesToUpdate += project.file("${subProj}/build.gradle.kts")
    }

    commentLines += "alias(libs.plugins.androidLibrary)"
    commentLines += "id(\"com.android.library\")"
    commentBlocks += "android"
}

tasks.register<UnCommentTask>("enableAndroidPlatform") {
    group = "build config"

    filesToUpdate += rootProject.file("buildSrc/src/main/kotlin/kool.androidlib-conventions.gradle.kts")
    listOf("kool-core", "kool-editor-model").forEach { subProj ->
        filesToUpdate += project.file("${subProj}/build.gradle.kts")
    }

    uncommentLines += "alias(libs.plugins.androidLibrary)"
    uncommentLines += "id(\"com.android.library\")"
    uncommentBlocks += "android"
}
