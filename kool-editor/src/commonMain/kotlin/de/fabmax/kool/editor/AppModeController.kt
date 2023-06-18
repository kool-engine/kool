package de.fabmax.kool.editor

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.api.AppMode
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.util.logI

class AppModeController(val editor: KoolEditor) {

    fun startApp() {
        logI { "Start app" }
        AppState.appModeState.set(AppMode.PLAY)
        EditorState.loadedApp.value?.app?.startApp(KoolSystem.requireContext())
    }

    fun togglePause() {
        if (AppState.isPlayMode) {
            if (AppState.appMode == AppMode.PLAY) {
                logI { "Pause app" }
                AppState.appModeState.set(AppMode.PAUSE)
            } else if (AppState.appMode == AppMode.PAUSE) {
                logI { "Unpause app" }
                AppState.appModeState.set(AppMode.PLAY)
            }
        }
    }

    fun stopApp() {
        logI { "Stop app" }
        AppState.appModeState.set(AppMode.EDIT)
        editor.appLoader.reloadApp()
    }

    fun resetApp() {
        logI { "Reset app" }
        EditorState.projectModel.reset()
        EditorState.loadedApp.value?.app?.resetApp(KoolSystem.requireContext())
    }

}