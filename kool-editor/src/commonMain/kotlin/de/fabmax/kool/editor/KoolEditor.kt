package de.fabmax.kool.editor

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.menu.EditorMenu
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.runOnMainThread

class KoolEditor(val ctx: KoolContext) {

    val loadedApp = MutableStateValue<AppContext?>(null)
    val appReloadListeners = mutableListOf<AppReloadListener>()

    val menu = EditorMenu(this)

    init {
        AppLoadService(this)
        ctx.scenes += menu
    }

    private fun bringEditorMenuToTop() {
        ctx.scenes -= menu
        ctx.scenes += menu
    }

    /**
     * Invoked by AppLoadService when the edited app has changed and was reloaded.
     */
    fun loadApp(app: EditorAwareApp) {
        runOnMainThread {
            val oldApp = loadedApp.value
            oldApp?.let { oldAppCtx ->
                ctx.scenes -= oldAppCtx.appScenes.toSet()
                oldAppCtx.appScenes.forEach { it.dispose(ctx) }
                oldAppCtx.app.onDispose(ctx, true)
            }

            val newApp = AppContext(app, app.startApp(ctx, true))
            loadedApp.set(newApp)
            ctx.scenes += newApp.appScenes

            bringEditorMenuToTop()
            appReloadListeners.forEach { it.onAppReload(oldApp, newApp) }
        }
    }

    class AppContext(val app: EditorAwareApp, val appScenes: List<Scene>)

    interface AppReloadListener {
        fun onAppReload(oldApp: AppContext?, newApp: AppContext)
    }
}