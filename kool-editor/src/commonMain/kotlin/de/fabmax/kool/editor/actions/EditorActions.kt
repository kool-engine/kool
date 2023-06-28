package de.fabmax.kool.editor.actions

object EditorActions {

    private const val UNDO_HISTORY_SIZE = 100

    private val actionStack = ArrayDeque<EditorAction>()
    private var actionPtr = -1

    fun clear() {
        actionStack.clear()
        actionPtr = -1
    }

    fun applyAction(action: EditorAction) {
        dropUndoneActions()
        actionStack.addLast(action)
        actionPtr++
        while (actionStack.size > UNDO_HISTORY_SIZE) {
            actionStack.removeFirst()
            actionPtr--
        }
        action.doAction()
    }

    fun undo() {
        if (actionPtr >= 0) {
            val undoAction = actionStack[actionPtr--]
            undoAction.undoAction()
        }
    }

    fun redo() {
        if (actionPtr < actionStack.lastIndex) {
            val redoAction = actionStack[++actionPtr]
            redoAction.doAction()
        }
    }

    private fun dropUndoneActions() {
        while (actionStack.lastIndex > actionPtr) {
            actionStack.removeLast()
        }
    }
}