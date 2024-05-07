package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.util.nodeModel

class MoveSceneNodeAction(
    moveNodeModels: List<SceneNodeModel>,
    val newParentId: NodeId,
    val insertionPos: NodeModel.InsertionPos
) : SceneNodeAction(moveNodeModels) {

    private val undoInfos = mutableMapOf<NodeId, UndoInfo>()

    init {
        moveNodeModels.forEach { moveNodeModel ->
            val undoIdx = moveNodeModel.parent.nodeData.childNodeIds.indexOf(moveNodeModel.nodeId)
            val undoPos = if (undoIdx > 0) {
                val after = moveNodeModel.sceneModel.nodeModels[moveNodeModel.parent.nodeData.childNodeIds[undoIdx - 1]]
                after?.let { NodeModel.InsertionPos.After(it.nodeId) } ?: NodeModel.InsertionPos.End
            } else {
                val before = moveNodeModel.sceneModel.nodeModels[moveNodeModel.parent.nodeData.childNodeIds.getOrNull(undoIdx + 1)]
                before?.let { NodeModel.InsertionPos.Before(it.nodeId) } ?: NodeModel.InsertionPos.End
            }
            undoInfos[moveNodeModel.nodeId] = UndoInfo(moveNodeModel.parent.nodeId, undoPos)
        }
    }

    override fun doAction() {
        val newParent = newParentId.nodeModel ?: return
        var insertionPos = this.insertionPos

        sceneNodes.forEach { nodeModel ->
            undoInfos[nodeModel.nodeId]?.let { undoInfo ->
                val oldParent = undoInfo.parent.nodeModel
                if (oldParent != null) {
                    oldParent.removeChild(nodeModel)
                    newParent.addChild(nodeModel, insertionPos)
                    insertionPos = NodeModel.InsertionPos.After(nodeModel.nodeId)
                }
            }
        }
        KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
    }

    override fun undoAction() {
        val oldParent = newParentId.nodeModel ?: return

        sceneNodes.forEach { nodeModel ->
            undoInfos[nodeModel.nodeId]?.let { undoInfo ->
                val newParent = undoInfo.parent.nodeModel
                if (newParent != null) {
                    oldParent.removeChild(nodeModel)
                    newParent.addChild(nodeModel, undoInfo.insertionPos)
                }
            }
        }
        KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
    }

    private class UndoInfo(val parent: NodeId, val insertionPos: NodeModel.InsertionPos)
}