package de.fabmax.kool.editor

import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.ui.BehaviorEditor
import de.fabmax.kool.util.launchOnMainThread
import de.fabmax.kool.util.logE
import kotlin.reflect.KClass

expect class AppLoadService(paths: ProjectPaths) {
    var hasAppChanged: Boolean
        private set

    fun addIgnorePath(path: String)

    suspend fun buildApp()

    suspend fun loadApp(): LoadedApp
}

interface AppReloadListener {
    suspend fun onAppReloaded(loadedApp: LoadedApp)
}

inline fun AppReloadListener(crossinline onAppReloaded: suspend (LoadedApp) -> Unit): AppReloadListener {
    return object : AppReloadListener {
        override suspend fun onAppReloaded(loadedApp: LoadedApp) {
            onAppReloaded(loadedApp)
        }
    }
}

class LoadedApp(val app: EditorAwareApp, val behaviorClasses: Map<KClass<*>, AppBehavior>)

class AppBehavior(val simpleName: String, val qualifiedName: String, val properties: List<BehaviorProperty>) {
    val prettyName = BehaviorEditor.camelCaseToWords(simpleName)

    fun dumpProperties() {
        println(qualifiedName)
        properties.forEach {
            println("    ${it.name}: ${it.type}")
        }
    }
}

class AppLoader(val editor: KoolEditor, paths: ProjectPaths) {
    val appReloadListeners = mutableListOf<AppReloadListener>()

    private val loadService = AppLoadService(paths)
    private var isBuildInProgress = false

    init {
        editor.editorContent.onUpdate {
            if (loadService.hasAppChanged) {
                editor.ui.appStateInfo.set("App sources changed on disc")
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
                        editor.ui.appStateInfo.set("Building app...")
                        loadService.buildApp()
                    }
                    editor.ui.appStateInfo.set("Loading app...")
                    val app = loadService.loadApp()
                    appReloadListeners.forEach { it.onAppReloaded(app) }
                } catch (e: Exception) {
                    editor.ui.appStateInfo.set("Failed loading app!")
                    logE { "Failed (re-)loading app: $e" }
                    e.printStackTrace()
                }
                isBuildInProgress = false
            }
        }
    }
}
