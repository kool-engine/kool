package de.fabmax.kool.editor.api

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.Scene

interface EditorAwareApp {

    fun startApp(ctx: KoolContext, isInEditor: Boolean): List<Scene>

}