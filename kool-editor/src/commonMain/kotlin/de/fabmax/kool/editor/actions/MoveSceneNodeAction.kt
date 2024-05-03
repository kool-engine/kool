package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneNodeModel

class MoveSceneNodeAction(
    moveNodeModel: SceneNodeModel,
    newParent: NodeModel,
    val insertionPos: NodeModel.InsertionPos
) : SceneNodeAction(listOf(moveNodeModel)) {

    private val newParentId = newParent.nodeId

    private val undoParentId = moveNodeModel.parent.nodeId
    private val undoInsertionPos: NodeModel.InsertionPos

    init {
        val oldIdx = moveNodeModel.parent.nodeData.childNodeIds.indexOf(moveNodeModel.nodeId)
        undoInsertionPos = if (oldIdx > 0) {
            val after = moveNodeModel.sceneModel.nodeModels[moveNodeModel.parent.nodeData.childNodeIds[oldIdx - 1]]
            after?.let { NodeModel.InsertionPos.After(it.nodeId) } ?: NodeModel.InsertionPos.End
        } else {
            val before = moveNodeModel.sceneModel.nodeModels[moveNodeModel.parent.nodeData.childNodeIds.getOrNull(oldIdx + 1)]
            before?.let { NodeModel.InsertionPos.Before(it.nodeId) } ?: NodeModel.InsertionPos.End
        }
    }

    override fun doAction() {
        val nodeModel = nodeModel ?: return
        val undoParent = resolveNodeModel(undoParentId) ?: return
        val newParent = resolveNodeModel(newParentId) ?: return

        undoParent.removeChild(nodeModel)
        newParent.addChild(nodeModel, insertionPos)
        KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
    }

    override fun undoAction() {
        val nodeModel = nodeModel ?: return
        val undoParent = resolveNodeModel(undoParentId) ?: return
        val newParent = resolveNodeModel(newParentId) ?: return

        newParent.removeChild(nodeModel)
        undoParent.addChild(nodeModel, undoInsertionPos)
        KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
    }
}