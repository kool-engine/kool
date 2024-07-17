package de.fabmax.kool.editor

import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.ui.BehaviorEditor
import de.fabmax.kool.util.launchOnMainThread
import de.fabmax.kool.util.logE
import kotlin.reflect.KClass

interface AppLoadService {
    suspend fun buildApp()
    suspend fun loadApp(): LoadedApp

    fun addChangeListener(listener: AppSourcesChangeListener)
}

expect fun AppLoadService(projectFiles: ProjectFiles): AppLoadService

interface AppReloadListener {
    suspend fun onAppReloaded(loadedApp: LoadedApp)
}

interface AppSourcesChangeListener {
    fun onAppSourcesChanged()
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

    fun printProperties() {
        println(qualifiedName)
        properties.forEach {
            println("    ${it.name}: ${it.kType}")
        }
    }
}

class AppLoader(val editor: KoolEditor) : AppSourcesChangeListener {
    val appReloadListeners = mutableListOf<AppReloadListener>()

    private val loadService = AppLoadService(editor.projectFiles)
    private var appSourcesChanged = false
    private var isBuildInProgress = false

    init {
        loadService.addChangeListener(this)
        editor.editorContent.onUpdate {
            if (appSourcesChanged) {
                editor.ui.appStateInfo.set("App sources changed on disc")
                if (!isBuildInProgress && editor.ctx.isWindowFocused) {
                    reloadApp()
                }
            }
        }
    }

    fun reloadApp() {
        if (!isBuildInProgress) {
            isBuildInProgress = true
            launchOnMainThread {
                try {
                    if (appSourcesChanged) {
                        appSourcesChanged = false
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

    override fun onAppSourcesChanged() {
        appSourcesChanged = true
    }
}
