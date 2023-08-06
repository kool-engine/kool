package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.editor.model.SceneNodeModel

class SetTransformAction(
    private val editedNodeModel: SceneNodeModel,
    private val oldTransform: TransformData,
    private val newTransform: TransformData
) : EditorAction {

    override fun doAction() {
        editedNodeModel.transform.transformState.set(newTransform)
        newTransform.toTransform(editedNodeModel.drawNode.transform)
    }

    override fun undoAction() {
        editedNodeModel.transform.transformState.set(oldTransform)
        oldTransform.toTransform(editedNodeModel.drawNode.transform)
    }
}