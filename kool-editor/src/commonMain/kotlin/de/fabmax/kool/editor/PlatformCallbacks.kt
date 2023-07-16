package de.fabmax.kool.editor

import de.fabmax.kool.KoolContext

expect object PlatformCallbacks {
    fun onEditorStarted(ctx: KoolContext)

    fun onWindowCloseRequest(ctx: KoolContext): Boolean
}