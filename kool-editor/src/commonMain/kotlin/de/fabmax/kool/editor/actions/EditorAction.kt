package de.fabmax.kool.editor.actions

interface EditorAction {
    fun doAction()
    fun undoAction()

    fun apply() {
        EditorActions.applyAction(this)
    }
}
