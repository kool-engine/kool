package de.fabmax.kool.editor.api

import de.fabmax.kool.modules.ui2.mutableStateOf

object AppState {

    var isInEditorState = mutableStateOf(false)
    var appModeState = mutableStateOf(AppMode.PLAY)

    val isInEditor: Boolean get() = isInEditorState.value
    val appMode: AppMode get() = appModeState.value

}

enum class AppMode {
    EDIT,
    PLAY,
    PAUSE
}