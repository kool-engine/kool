package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.ui.SceneObjectTree
import de.fabmax.kool.util.launchOnMainThread

class RemoveNodeAction(
    private val removeNodeModel: SceneNodeModel,
    private val parentNodeModel: EditorNodeModel,
    private val parentSceneModel: SceneModel,
    private val sceneTree: SceneObjectTree
) : EditorAction {

    override fun apply() {
        if (EditorState.selectedNode.value == removeNodeModel) {
            EditorState.selectedNode.set(null)
        }
        parentSceneModel.removeSceneNode(removeNodeModel, parentNodeModel)
        sceneTree.refreshSceneTree()
    }

    override fun undo() {
        // fixme: this will not work in case removed node has children, because children will not be present in scene
        //  anymore -> deepcopy child node models before removal and re-add them in correct order on undo
        launchOnMainThread {
            parentSceneModel.addSceneNode(removeNodeModel, parentNodeModel)
            sceneTree.refreshSceneTree()
        }
    }
}