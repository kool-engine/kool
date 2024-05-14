package de.fabmax.kool.editor.actions

class FusedAction(val subActions: List<EditorAction>) : EditorAction {
    override fun doAction() = subActions.forEach { it.doAction() }
    override fun undoAction() = subActions.forEach { it.undoAction() }
}