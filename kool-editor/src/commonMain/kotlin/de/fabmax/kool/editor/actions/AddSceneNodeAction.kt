package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.data.SceneNodeData
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.util.nodeModel
import de.fabmax.kool.editor.util.sceneModel
import de.fabmax.kool.editor.util.sceneNodeModel
import de.fabmax.kool.util.launchOnMainThread

class AddSceneNodeAction(
    val addNodeDatas: List<SceneNodeData>,
    val parentId: NodeId
) : EditorAction {

    override fun doAction() {
        val parent = parentId.nodeModel ?: return
        val scene = parent.sceneModel

        launchOnMainThread {
            val topLevelNodes = addNodeDatas.associateBy { it.nodeId }.toMutableMap()
            addNodeDatas.forEach {
                KoolEditor.instance.projectModel.addSceneNodeData(it)
                topLevelNodes -= it.childNodeIds.toSet()
            }
            topLevelNodes.values.forEach {
                scene.addSceneNode(SceneNodeModel(it, parent, scene))
            }
            refreshComponentViews()
        }
    }

    override fun undoAction() {
        val scene = parentId.nodeModel?.sceneModel ?: return
        val nodes = addNodeDatas.mapNotNull { it.nodeId.sceneNodeModel }
        val nonChildNodes = DeleteSceneNodesAction.removeChildNodes(nodes)

        KoolEditor.instance.selectionOverlay.reduceSelection(nodes)
        nonChildNodes.forEach { scene.removeSceneNode(it) }

        refreshComponentViews()
    }
}