package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.EditorModelComponent
import de.fabmax.kool.editor.model.EditorNodeModel

class AddComponentAction(
    val nodeModel: EditorNodeModel,
    val addComponent: EditorModelComponent
) : EditorAction {

    override fun apply() = nodeModel.addComponent(addComponent)
    override fun undo() = nodeModel.removeComponent(addComponent)

}