package de.fabmax.kool.editor

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.runOnMainThread

class KoolEditor(val ctx: KoolContext) {

    private var loadedApp: AppContext? = null

    init {
        AppLoadService(this)
    }

    fun loadApp(app: EditorAwareApp) {
        runOnMainThread {
            val newAppCtx = AppContext(app, app.startApp(ctx, true))

            loadedApp?.let {  oldAppCtx ->
                ctx.scenes -= oldAppCtx.appScenes.toSet()
                oldAppCtx.appScenes.forEach { it.dispose(ctx) }
            }
            ctx.scenes += newAppCtx.appScenes
        }
    }

    private class AppContext(val app: EditorAwareApp, val appScenes: List<Scene>)
}