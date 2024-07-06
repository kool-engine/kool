package de.fabmax.kool.editor

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.NativeAssetLoader
import de.fabmax.kool.util.logE
import kotlinx.coroutines.launch

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object PlatformFunctions {

    actual val windowButtonStyle: WindowButtonStyle = WindowButtonStyle.NONE
    actual val isWindowMaximized: Boolean = false

    var loadedApp: LoadedApp? = null

    fun initPlatform(loadedApp: LoadedApp) {
        Assets.defaultLoader = NativeAssetLoader(".")
        this.loadedApp = loadedApp
    }

    actual fun onEditorStarted(ctx: KoolContext) { }

    actual fun onExit(ctx: KoolContext) { }

    actual fun editBehavior(behaviorClassName: String) {
        logE { "Source editing is not available on JS. Download project and use the JVM variant." }
    }

    actual suspend fun chooseFilePath(): String? {
        logE { "chooseFilePath() not available on JS" }
        return null
    }

    actual fun saveProjectBlocking() {
        Assets.launch {
            KoolEditor.instance.saveProject()
        }
    }

    actual fun toggleMaximizeWindow() { }
    actual fun minimizeWindow() { }
    actual fun closeWindow() { }
}