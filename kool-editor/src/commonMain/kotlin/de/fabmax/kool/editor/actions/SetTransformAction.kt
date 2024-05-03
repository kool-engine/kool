package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.editor.model.SceneNodeModel

class SetTransformAction(
    nodeModels: List<SceneNodeModel>,
    private val undoTransforms: List<TransformData>,
    private val applyTransforms: List<TransformData>
) : SceneNodeAction(nodeModels) {

    constructor(
        nodeModel: SceneNodeModel,
        undoTransform: TransformData,
        applyTransform: TransformData
    ) : this(listOf(nodeModel), listOf(undoTransform), listOf(applyTransform))

    override fun doAction() {
        nodeModels.forEachIndexed { i, nodeModel ->
            val applyTransform = applyTransforms[i]
            nodeModel.transform.transformState.set(applyTransform)
            applyTransform.toTransform(nodeModel.drawNode.transform)
        }
    }

    override fun undoAction() {
        nodeModels.forEachIndexed { i, nodeModel ->
            val undoTransform = undoTransforms[i]
            nodeModel.transform.transformState.set(undoTransform)
            undoTransform.toTransform(nodeModel.drawNode.transform)
        }
    }
}