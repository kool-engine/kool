package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.model.MScene
import de.fabmax.kool.editor.model.MSceneNode
import de.fabmax.kool.editor.ui.SceneObjectTree
import de.fabmax.kool.util.launchOnMainThread

class AddObjectAction(
    private val addNodeModel: MSceneNode,
    private val parentSceneModel: MScene,
    private val sceneTree: SceneObjectTree
) : EditorAction {

    override fun apply() {
        launchOnMainThread {
            parentSceneModel.addSceneNode(addNodeModel)
            sceneTree.refreshSceneTree()
        }
    }

    override fun undo() {
        if (EditorState.selectedObject.value == addNodeModel) {
            EditorState.selectedObject.set(null)
        }
        parentSceneModel.removeSceneNode(addNodeModel)
        sceneTree.refreshSceneTree()
    }
}