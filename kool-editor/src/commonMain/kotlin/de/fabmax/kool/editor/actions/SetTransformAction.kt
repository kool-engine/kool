package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.editor.model.MSceneNode
import de.fabmax.kool.math.Mat4d

class SetTransformAction(
    private val editedNodeModel: MSceneNode,
    oldTransform: Mat4d,
    newTransform: Mat4d
) : EditorAction {

    private val oldTransform = Mat4d().set(oldTransform)
    private val newTransform = Mat4d().set(newTransform)

    override fun apply() {
        editedNodeModel.nodeData.transform = TransformData(newTransform)
        editedNodeModel.node.transform.set(newTransform)
    }

    override fun undo() {
        editedNodeModel.nodeData.transform = TransformData(oldTransform)
        editedNodeModel.node.transform.set(oldTransform)
    }
}