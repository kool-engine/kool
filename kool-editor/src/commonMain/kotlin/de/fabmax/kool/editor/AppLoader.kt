package de.fabmax.kool.editor

import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.util.launchOnMainThread
import de.fabmax.kool.util.logE

expect class AppLoadService() {
    var hasAppChanged: Boolean
        private set

    suspend fun buildApp()

    suspend fun loadApp(): EditorAwareApp
}

class AppLoader(val editor: KoolEditor) {
    val appReloadListeners = mutableListOf<(EditorAwareApp) -> Unit>()

    private val loadService = AppLoadService()
    private var isBuildInProgress = false

    init {
        editor.editorContent.onUpdate {
            if (loadService.hasAppChanged && !isBuildInProgress && editor.ctx.isWindowFocused) {
                reloadApp()
            }
        }
    }

    fun reloadApp() {
        if (!isBuildInProgress) {
            isBuildInProgress = true
            launchOnMainThread {
                try {
                    if (loadService.hasAppChanged) {
                        loadService.buildApp()
                    }
                    val app = loadService.loadApp()
                    appReloadListeners.forEach { it(app) }
                } catch (e: Exception) {
                    logE { "Failed (re-)loading app: $e" }
                }
                isBuildInProgress = false
            }
        }
    }
}
