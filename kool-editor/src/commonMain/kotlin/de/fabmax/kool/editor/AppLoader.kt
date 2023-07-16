package de.fabmax.kool.editor

import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.ui.ScriptEditor
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

fun interface AppReloadListener {
    suspend fun onAppReloaded(loadedApp: LoadedApp)
}

class LoadedApp(val app: EditorAwareApp, val scriptClasses: Map<KClass<*>, AppScript>)

class AppScript(klass: KClass<*>, val properties: List<ScriptProperty>) {
    val qualifiedName = klass.qualifiedName ?: "<unknown>"
    val simpleName = klass.simpleName
    val prettyName = ScriptEditor.camelCaseToWords(klass.simpleName ?: "<unknown>")

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
