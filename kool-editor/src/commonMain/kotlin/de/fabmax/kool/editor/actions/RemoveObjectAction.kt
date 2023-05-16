package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.model.MScene
import de.fabmax.kool.editor.model.MSceneNode
import de.fabmax.kool.editor.ui.SceneObjectTree
import de.fabmax.kool.util.launchOnMainThread

class RemoveObjectAction(
    private val removeNodeModel: MSceneNode,
    private val parentSceneModel: MScene,
    private val sceneTree: SceneObjectTree
) : EditorAction {

    override fun apply() {
        if (EditorState.selectedObject.value == removeNodeModel) {
            EditorState.selectedObject.set(null)
        }
        parentSceneModel.removeSceneNode(removeNodeModel)
        sceneTree.refreshSceneTree()
    }

    override fun undo() {
        // fixme: this will not work in case removed node has children, because children will not be present in scene
        //  anymore -> deepcopy child node models before removal and re-add them in correct order on undo
        launchOnMainThread {
            parentSceneModel.addSceneNode(removeNodeModel)
            sceneTree.refreshSceneTree()
        }
    }
}