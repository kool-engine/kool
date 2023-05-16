package de.fabmax.kool.editor

import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.util.launchOnMainThread
import de.fabmax.kool.util.logE

expect class AppLoadService(watchDirs: Set<String>, appLoadClassPath: String) {
    var hasAppChanged: Boolean
        private set

    fun addIgnorePath(path: String)

    suspend fun buildApp()

    suspend fun loadApp(): EditorAwareApp
}

fun interface AppReloadListener {
    suspend fun onAppReloaded(loadedApp: EditorAwareApp)
}

class AppLoader(val editor: KoolEditor, watchDirs: Set<String>, appLoadClassPath: String) {
    val appReloadListeners = mutableListOf<AppReloadListener>()

    private val loadService = AppLoadService(watchDirs, appLoadClassPath)
    private var isBuildInProgress = false

    init {
        editor.editorContent.onUpdate {
            if (loadService.hasAppChanged) {
                editor.ui.appLoaderState.set("App sources changed on disc")
                if (!isBuildInProgress && editor.ctx.isWindowFocused) {
                    reloadApp()
                }
            }
        }
    }

    fun addIgnorePath(path: String) {
        loadService.addIgnorePath(path)
    }

    fun reloadApp() {
        if (!isBuildInProgress) {
            isBuildInProgress = true
            launchOnMainThread {
                try {
                    if (loadService.hasAppChanged) {
                        editor.ui.appLoaderState.set("Building app...")
                        loadService.buildApp()
                    }
                    editor.ui.appLoaderState.set("Loading app...")
                    val app = loadService.loadApp()
                    editor.ui.appLoaderState.set("App is running")
                    appReloadListeners.forEach { it.onAppReloaded(app) }
                } catch (e: Exception) {
                    editor.ui.appLoaderState.set("Failed loading app!")
                    logE { "Failed (re-)loading app: $e" }
                }
                isBuildInProgress = false
            }
        }
    }
}
