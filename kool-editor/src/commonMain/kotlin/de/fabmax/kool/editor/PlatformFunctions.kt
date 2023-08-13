package de.fabmax.kool.editor

import de.fabmax.kool.KoolContext

expect object PlatformFunctions {
    fun onEditorStarted(ctx: KoolContext)

    fun onWindowCloseRequest(ctx: KoolContext): Boolean

    fun editBehavior(behaviorSourcePath: String)
}