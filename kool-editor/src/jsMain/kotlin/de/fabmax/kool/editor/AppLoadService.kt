package de.fabmax.kool.editor

actual class AppLoadService actual constructor(paths: ProjectPaths) {
    actual var hasAppChanged = false
        private set

    actual fun addIgnorePath(path: String) { }

    actual suspend fun buildApp() { }

    actual suspend fun loadApp(): LoadedApp {
        return PlatformFunctions.loadedApp ?: throw IllegalStateException("PlatformFunctions.initPlatform() not called")
    }
}
