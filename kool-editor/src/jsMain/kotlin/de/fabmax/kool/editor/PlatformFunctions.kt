package de.fabmax.kool.editor

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.model.EditorProject
import de.fabmax.kool.util.logE

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object PlatformFunctions {

    private var projectModel: EditorProject? = null
    var loadedApp: LoadedApp? = null

    suspend fun initPlatform(projModelPath: String, loadedApp: LoadedApp) {
        Assets.assetsBasePath = "."
        projectModel = EditorProject.loadFromAssets(projModelPath)
        this.loadedApp = loadedApp
    }

    actual fun onEditorStarted(ctx: KoolContext) { }

    actual fun onWindowCloseRequest(ctx: KoolContext): Boolean = true

    actual fun editBehavior(behaviorSourcePath: String) {
        logE { "Source editing is not available on JS. Download project and use the JVM variant." }
    }

    actual fun loadProjectModel(path: String): EditorProject? {
        return projectModel
    }

    actual fun saveProjectModel(path: String) {
        logE { "saveProjectModel not yet implemented" }
    }

    actual suspend fun chooseFilePath(): String? {
        logE { "chooseFilePath() not available on JS" }
        return null
    }
}