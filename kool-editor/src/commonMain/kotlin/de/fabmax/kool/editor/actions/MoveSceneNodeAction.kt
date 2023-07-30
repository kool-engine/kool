package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneNodeModel

class MoveSceneNodeAction(
    val moveNode: SceneNodeModel,
    val newParent: NodeModel,
    val insertionPos: NodeModel.InsertionPos
) : EditorAction {

    val undoParent = moveNode.parent
    val undoInsertionPos: NodeModel.InsertionPos

    init {
        val oldIdx = moveNode.parent.nodeData.childNodeIds.indexOf(moveNode.nodeId)
        undoInsertionPos = if (oldIdx > 0) {
            val after = moveNode.sceneModel.nodeModels[moveNode.parent.nodeData.childNodeIds[oldIdx - 1]]
            after?.let { NodeModel.InsertionPos.After(it) } ?: NodeModel.InsertionPos.End
        } else {
            val before = moveNode.sceneModel.nodeModels[moveNode.parent.nodeData.childNodeIds.getOrNull(oldIdx + 1)]
            before?.let { NodeModel.InsertionPos.Before(it) } ?: NodeModel.InsertionPos.End
        }
    }

    override fun doAction() {
        undoParent.removeChild(moveNode)
        newParent.addChild(moveNode, insertionPos)
        KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
    }

    override fun undoAction() {
        newParent.removeChild(moveNode)
        undoParent.addChild(moveNode, undoInsertionPos)
        KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
    }
}