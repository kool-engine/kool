package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneNodeModel

class RenameNodeAction(
    nodeModel: NodeModel,
    val applyName: String,
    val undoName: String
) : EditorAction {

    private val nodeId = nodeModel.nodeId
    private val sceneId = if (nodeModel is SceneNodeModel) nodeModel.sceneModel.nodeId else nodeModel.nodeId
    private val nodeModel: NodeModel? get() {
        val scene = sceneModel(sceneId)
        return if (nodeId == sceneId) scene else scene?.nodeModels?.get(nodeId)
    }

    override fun doAction() {
        nodeModel?.let {
            it.nameState.set(applyName)
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }

    override fun undoAction() {
        nodeModel?.let {
            it.nameState.set(undoName)
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }
}