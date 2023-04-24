package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.model.MScene
import de.fabmax.kool.editor.model.MSceneNode
import de.fabmax.kool.editor.ui.SceneObjectTree

class AddObjectAction(
    private val addNode: MSceneNode<*>,
    private val parentNode: MSceneNode<*>,
    private val parentScene: MScene,
    private val sceneTree: SceneObjectTree
) : EditorAction {

    override fun apply() {
        parentScene.addSceneNode(addNode, parentNode)
        sceneTree.refreshSceneTree()
    }

    override fun undo() {
        if (EditorState.selectedObject.value == addNode) {
            EditorState.selectedObject.set(null)
        }
        parentScene.removeSceneNode(addNode, parentNode)
        sceneTree.refreshSceneTree()
    }
}