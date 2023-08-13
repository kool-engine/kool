package de.fabmax.kool.editor

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.model.EditorProject

expect object PlatformFunctions {
    fun onEditorStarted(ctx: KoolContext)

    fun onWindowCloseRequest(ctx: KoolContext): Boolean

    fun editBehavior(behaviorSourcePath: String)

    fun loadProjectModel(path: String): EditorProject

    fun saveProjectModel(path: String)

    suspend fun chooseFilePath(): String?
}