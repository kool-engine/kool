package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.model.EditorModelComponent
import de.fabmax.kool.editor.model.EditorNodeModel

class RemoveComponentAction(
    val nodeModel: EditorNodeModel,
    val removeComponent: EditorModelComponent
) : EditorAction {

    override fun apply() = nodeModel.removeComponent(removeComponent)
    override fun undo() = nodeModel.addComponent(removeComponent)

}