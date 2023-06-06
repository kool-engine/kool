package de.fabmax.kool.editor

data class ProjectPaths(
    val projectDir: String,
    val projectFile: String = "${projectDir}/src/commonMain/resources/kool-project.json",

    val gradleRootDir: String = projectDir,
    val gradleBuildTask: String = "jvmMainClasses",

    val assetsPath: String = "$projectDir/src/commonMain/resources",

    val srcPaths: Set<String> = setOf(
        "${projectDir}/src/commonMain/kotlin",
        "${projectDir}/src/jsMain/kotlin",
        "${projectDir}/src/jvmMain/kotlin"
    ),

    val classPath: String = "${projectDir}/build/classes/kotlin/jvm/main",
    val appMainClass: String = "de.fabmax.kool.app.App"
)