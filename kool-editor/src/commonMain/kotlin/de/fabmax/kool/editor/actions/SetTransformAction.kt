package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.Mat4d

class SetTransformAction(
    private val editedNodeModel: SceneNodeModel,
    oldTransform: Mat4d,
    newTransform: Mat4d
) : EditorAction {

    private val oldTransform = Mat4d().set(oldTransform)
    private val newTransform = Mat4d().set(newTransform)

    override fun apply() {
        editedNodeModel.transform.transformState.set(TransformData(newTransform))
        editedNodeModel.drawNode.transform.set(newTransform)
    }

    override fun undo() {
        editedNodeModel.transform.transformState.set(TransformData(oldTransform))
        editedNodeModel.drawNode.transform.set(oldTransform)
    }
}