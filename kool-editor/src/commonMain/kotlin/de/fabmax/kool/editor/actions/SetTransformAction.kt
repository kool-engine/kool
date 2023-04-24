package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.model.MSceneNode
import de.fabmax.kool.editor.model.MTransform
import de.fabmax.kool.math.Mat4d

class SetTransformAction(
    private val editedNodeModel: MSceneNode<*>,
    oldTransform: Mat4d,
    newTransform: Mat4d
) : EditorAction {

    private val oldTransform = Mat4d().set(oldTransform)
    private val newTransform = Mat4d().set(newTransform)

    override fun apply() {
        editedNodeModel.nodeProperties.transform = MTransform(newTransform)
        editedNodeModel.created?.transform?.set(newTransform)
    }

    override fun undo() {
        editedNodeModel.nodeProperties.transform = MTransform(oldTransform)
        editedNodeModel.created?.transform?.set(oldTransform)
    }
}