package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.util.launchOnMainThread

class DeleteNodeAction(
    private val removeNodeModel: SceneNodeModel
) : EditorAction {

    override fun doAction() {
        if (EditorState.selectedNode.value == removeNodeModel) {
            EditorState.selectedNode.set(null)
        }
        removeNodeModel.scene.removeSceneNode(removeNodeModel)
        KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
    }

    override fun undoAction() {
        // fixme: this will not work in case removed node has children, because children will not be present in scene
        //  anymore -> deepcopy child node models before removal and re-add them in correct order on undo
        launchOnMainThread {
            removeNodeModel.scene.addSceneNode(removeNodeModel)
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }
}

fun EditorState.deleteSelectedNode() {
    val node = selectedNode.value as? SceneNodeModel ?: return
    DeleteNodeAction(node).apply()
}
