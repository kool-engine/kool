package de.fabmax.kool.editor.api

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.model.MProject

interface EditorAwareApp {

    /**
     * Called on app initialization - either invoked by the KoolEditor ([isInEditor] = true) when the app was
     * (re-)loaded or by the Launcher ([isInEditor] = false) when the app was started in standalone mode.
     */
    suspend fun startApp(projectModel: MProject, isInEditor: Boolean, ctx: KoolContext)

    /**
     * Called on app shutdown - either because the app reloaded and the old version is about to be discarded or the
     * application is about to close.
     */
    fun onDispose(isInEditor: Boolean, ctx: KoolContext) { }

}