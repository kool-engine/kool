package de.fabmax.kool.editor

import de.fabmax.kool.util.logE

actual fun AppLoadService(projectFiles: ProjectFiles): AppLoadService = AppLoadServiceImpl()

class AppLoadServiceImpl : AppLoadService {
    override suspend fun buildApp() {
        logE { "App building not supported in browser" }
    }

    override suspend fun loadApp(): LoadedApp {
        return PlatformFunctions.loadedApp ?: throw IllegalStateException("PlatformFunctions.initPlatform() not called")
    }

    override fun addChangeListener(listener: AppSourcesChangeListener) { }
}
