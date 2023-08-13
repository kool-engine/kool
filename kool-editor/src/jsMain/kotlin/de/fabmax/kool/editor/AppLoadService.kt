package de.fabmax.kool.editor

actual class AppLoadService actual constructor(val paths: ProjectPaths) {
    actual var hasAppChanged = false
        private set

    actual fun addIgnorePath(path: String) { }

    actual suspend fun buildApp() { }

    actual suspend fun loadApp(): LoadedApp {
        return LoadedAppProxy.loadedApp ?: throw IllegalStateException("LoadedAppProxy.loadedApp not initialized")
    }
}

object LoadedAppProxy {
    var loadedApp: LoadedApp? = null
}
