package de.fabmax.kool.editor.api

import de.fabmax.kool.modules.ui2.mutableStateOf

object AppState {

    var isInEditorState = mutableStateOf(false)
    var isEditModeState = mutableStateOf(false)

    val isInEditor: Boolean get() = isInEditorState.value
    val isEditMode: Boolean get() = isEditModeState.value

}