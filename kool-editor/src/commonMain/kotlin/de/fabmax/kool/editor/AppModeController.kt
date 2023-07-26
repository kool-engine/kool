package de.fabmax.kool.editor

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.api.AppMode
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.util.logI

class AppModeController(val editor: KoolEditor) {

    fun startApp() {
        val app = EditorState.loadedApp.value?.app ?: return
        val sceneModel = EditorState.projectModel.getCreatedScenes().getOrNull(0) ?: return

        logI { "Start app" }

        // fixme: a bit hacky currently: restore app scene camera (was replaced by custom editor cam during
        //  editor app load)
        sceneModel.cameraState.value?.camera?.let { cam ->
            sceneModel.drawNode.camera = cam
        }

        AppState.appModeState.set(AppMode.PLAY)
        app.startApp(KoolSystem.requireContext())
        editor.setEditorOverlayVisibility(false)
        editor.ui.appStateInfo.set("App is running")
    }

    fun togglePause() {
        if (AppState.isPlayMode) {
            if (AppState.appMode == AppMode.PLAY) {
                logI { "Pause app" }
                AppState.appModeState.set(AppMode.PAUSE)
                editor.ui.appStateInfo.set("App is paused")
            } else if (AppState.appMode == AppMode.PAUSE) {
                logI { "Unpause app" }
                AppState.appModeState.set(AppMode.PLAY)
                editor.ui.appStateInfo.set("App is running")
            }
        }
    }

    fun stopApp() {
        logI { "Stop app" }
        AppState.appModeState.set(AppMode.EDIT)
        editor.setEditorOverlayVisibility(true)
        editor.appLoader.reloadApp()
    }

    fun resetApp() {
        logI { "Reset app" }
        editor.appLoader.reloadApp()
    }

}