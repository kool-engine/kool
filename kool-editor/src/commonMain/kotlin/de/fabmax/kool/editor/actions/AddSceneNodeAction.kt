package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.data.SceneNodeData
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.util.launchOnMainThread

class AddSceneNodeAction(
    val addNodeDatas: List<SceneNodeData>,
    val parentId: NodeId,
    val sceneId: NodeId
) : EditorAction {

    private val parentModel: NodeModel? get() {
        val scene = sceneModel(sceneId)
        return if (parentId == sceneId) scene else scene?.nodeModels?.get(parentId)
    }

    override fun doAction() {
        val scene = sceneModel(sceneId) ?: return
        val parent = parentModel ?: return

        launchOnMainThread {
            addNodeDatas.forEach {
                scene.addSceneNode(SceneNodeModel(it, parent, scene))
            }
            refreshComponentViews()
        }
    }

    override fun undoAction() {
        val scene = sceneModel(sceneId) ?: return
        addNodeDatas.forEach {
            sceneNodeModel(it.nodeId, sceneId)?.let { nodeModel ->
                if (nodeModel in KoolEditor.instance.selectionOverlay.selection) {
                    KoolEditor.instance.selectionOverlay.selection -= nodeModel
                }
                scene.removeSceneNode(nodeModel)
            }
        }
        refreshComponentViews()
    }
}