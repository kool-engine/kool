package de.fabmax.kool.editor.api

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.Scene

interface EditorAwareApp {

    /**
     * Called on app initialization - either invoked by the KoolEditor ([isInEditor] = true) when the app was
     * (re-)loaded or by the Launcher ([isInEditor] = false) when the app was started in standalone mode. Returns the
     * list of programmatically created content scenes.
     */
    fun startApp(ctx: KoolContext, isInEditor: Boolean): List<Scene> = emptyList()

    /**
     * Called on app shutdown - either because the app reloaded and the old version is about to be discarded or the
     * application is about to close.
     */
    fun onDispose(ctx: KoolContext, isInEditor: Boolean) { }

}