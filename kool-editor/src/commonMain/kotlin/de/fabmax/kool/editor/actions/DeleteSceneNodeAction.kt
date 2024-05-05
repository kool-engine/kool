package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.data.SceneNodeData
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.util.nodeModel
import de.fabmax.kool.editor.util.sceneModel
import de.fabmax.kool.util.launchOnMainThread

class DeleteSceneNodeAction(
    nodeModels: List<SceneNodeModel>
) : SceneNodeAction(nodeModels) {

    private val removeNodeInfos = nodeModels.map {
        val nodeIdx = it.parent.nodeData.childNodeIds.indexOf(it.nodeId)
        val pos = if (nodeIdx < it.parent.nodeData.childNodeIds.lastIndex) {
            NodeModel.InsertionPos.Before(it.parent.nodeData.childNodeIds[nodeIdx + 1])
        } else {
            NodeModel.InsertionPos.End
        }
        NodeInfo(it.nodeData, it.parent.nodeId, pos)
    }

    constructor(removeNodeModel: SceneNodeModel): this(listOf(removeNodeModel))

    override fun doAction() {
        KoolEditor.instance.selectionOverlay.selection.removeAll(sceneNodes)
        sceneNodes.forEach {
            it.sceneModel.removeSceneNode(it)
        }
        refreshComponentViews()
    }

    override fun undoAction() {
        // fixme: this will not work in case removed nodes have children, because children will not be present in scene
        //  anymore -> deepcopy child node models before removal and re-add them in correct order on undo
        launchOnMainThread {
            // removed node model was destroyed, crate a new one only using the old data
            removeNodeInfos.forEach { (nodeData, parentId, pos) ->
                parentId.nodeModel?.let { parent ->
                    val scene = parent.sceneModel
                    val node = SceneNodeModel(nodeData, parent, scene)
                    scene.addSceneNode(node)
                    parent.removeChild(node)
                    parent.addChild(node, pos)
                }
            }
            refreshComponentViews()
        }
    }

    private data class NodeInfo(val nodeData: SceneNodeData, val parentId: NodeId, val position: NodeModel.InsertionPos)
}
