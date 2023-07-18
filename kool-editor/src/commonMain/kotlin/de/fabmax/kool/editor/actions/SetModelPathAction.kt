package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ModelComponent

class SetModelPathAction(
    private val editComponent: ModelComponent,
    private val newPath: String,
) : EditorAction {

    private val oldPath = editComponent.modelPathState.value

    override fun doAction() = editComponent.modelPathState.set(newPath)

    override fun undoAction() = editComponent.modelPathState.set(oldPath)
}