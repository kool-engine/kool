package de.fabmax.kool.editor

import de.fabmax.kool.modules.filesystem.WritableFileSystem
import de.fabmax.kool.modules.filesystem.getFile

class ProjectFiles(val fileSystem: WritableFileSystem) {

    val projectModelFile = fileSystem.getFile("src/commonMain/resources/kool-project.json")
    val assets = fileSystem.getOrCreateDirectory("src/commonMain/resources/assets")

    val hasGradle = "build.gradle.kts" in fileSystem

}

/*

data class ProjectPaths(
    val projectDir: String,
    val projectFile: String = "${projectDir}/src/commonMain/resources/kool-project.json",

    val gradleRootDir: String = projectDir,
    val gradleBuildTask: String = "jvmMainClasses",

    /**
     * Assets base dir of all assets / files available via kool asset loader (incl. project files, etc).
     */
    val assetsBasePath: String = "$projectDir/src/commonMain/resources",

    /**
     * Subdirectory within [assetsBasePath] containing "usable" assets (textures, etc.) selectable via the
     * asset browser.
     */
    val assetsSubDir: String = "assets",

    val commonSrcPath: String = "${projectDir}/src/commonMain/kotlin",
    val jvmSrcPath: String = "${projectDir}/src/jvmMain/kotlin",
    val jsSrcPath: String = "${projectDir}/src/jsMain/kotlin",
    val srcPaths: Set<String> = setOf(commonSrcPath, jvmSrcPath, jsSrcPath),

    val jsAppBehaviorBindingsPath: String? = "$projectDir/src/jsMain/kotlin/BehaviorBindings.kt",

    val classPath: String = "${projectDir}/build/classes/kotlin/jvm/main",
    val appMainClass: String = "de.fabmax.kool.app.App"
)
 */