package de.fabmax.kool.editor

import de.fabmax.kool.modules.filesystem.WritableFileSystem
import de.fabmax.kool.modules.filesystem.getOrCreateDirectory

class ProjectFiles(
    val fileSystem: WritableFileSystem,
    val appMainClass: String = "de.fabmax.kool.app.App"
) {
    val projectModelDir = fileSystem.getOrCreateDirectory("src/commonMain/resources/project")
    val assets = fileSystem.getOrCreateDirectory("src/commonMain/resources/assets")
}
