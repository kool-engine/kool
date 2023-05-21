package de.fabmax.kool.editor.api

import de.fabmax.kool.ApplicationCallbacks
import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.model.EditorProject
import de.fabmax.kool.util.launchOnMainThread

interface EditorAwareApp {

    /**
     * Called on app initialization - either invoked by the KoolEditor when the app was
     * (re-)loaded or by the Launcher when the app was started in standalone mode. The two modes can be distinguished
     * via [AppState.isInEditor]
     */
    suspend fun startApp(projectModel: EditorProject, ctx: KoolContext)

    /**
     * Called on app shutdown - either because the app reloaded and the old version is about to be discarded or the
     * application is about to close.
     */
    fun onDispose(ctx: KoolContext) { }

    fun launchStandalone(ctx: KoolContext) {
        launchOnMainThread {
            val projModel = EditorProject.loadFromAssets() ?: throw IllegalStateException("kool-project.json not found")
            startApp(projModel, ctx)
            val createdScenes = projModel.getCreatedScenes()
            createdScenes.forEach {
                ctx.scenes += it.node
            }
        }

        ctx.applicationCallbacks = object : ApplicationCallbacks {
            override fun onWindowCloseRequest(ctx: KoolContext): Boolean {
                onDispose(ctx)
                return true
            }
        }
    }
}