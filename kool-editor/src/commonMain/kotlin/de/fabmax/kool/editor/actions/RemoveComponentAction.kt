package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.EditorModelComponent
import de.fabmax.kool.editor.model.EditorNodeModel

class RemoveComponentAction(
    val nodeModel: EditorNodeModel,
    val removeComponent: EditorModelComponent
) : EditorAction {

    override fun doAction() = nodeModel.removeComponent(removeComponent)
    override fun undoAction() = nodeModel.addComponent(removeComponent)

}