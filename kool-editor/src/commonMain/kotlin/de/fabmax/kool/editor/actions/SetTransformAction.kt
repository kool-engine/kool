package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.TransformData

fun SetTransformAction(
    nodeModel: GameEntity,
    undoTransform: TransformData,
    applyTransform: TransformData
) = SetTransformAction(listOf(nodeModel), listOf(undoTransform), listOf(applyTransform))

class SetTransformAction(
    nodeModels: List<GameEntity>,
    private val undoTransforms: List<TransformData>,
    private val applyTransforms: List<TransformData>
) : GameEntityAction(nodeModels) {

    override fun doAction() {
        gameEntities.forEachIndexed { i, nodeModel ->
            val applyTransform = applyTransforms[i]
            nodeModel.transform.transformState.set(applyTransform)
            applyTransform.toTransform(nodeModel.drawNode.transform)
        }
    }

    override fun undoAction() {
        gameEntities.forEachIndexed { i, nodeModel ->
            val undoTransform = undoTransforms[i]
            nodeModel.transform.transformState.set(undoTransform)
            undoTransform.toTransform(nodeModel.drawNode.transform)
        }
    }
}