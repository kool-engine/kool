package de.fabmax.kool.editor

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

    val srcPaths: Set<String> = setOf(
        "${projectDir}/src/commonMain/kotlin",
        "${projectDir}/src/jsMain/kotlin",
        "${projectDir}/src/jvmMain/kotlin"
    ),
    val jsAppScriptsPath: String? = "$projectDir/src/jsMain/kotlin/AppScripts.kt",

    val classPath: String = "${projectDir}/build/classes/kotlin/jvm/main",
    val appMainClass: String = "de.fabmax.kool.app.App"
)