package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.model.MScene
import de.fabmax.kool.editor.model.MSceneNode
import de.fabmax.kool.editor.ui.SceneObjectTree

class RemoveObjectAction(
    private val removeNode: MSceneNode<*>,
    private val parentNode: MSceneNode<*>,
    private val parentScene: MScene,
    private val sceneTree: SceneObjectTree
) : EditorAction {

    override fun apply() {
        if (EditorState.selectedObject.value == removeNode) {
            EditorState.selectedObject.set(null)
        }
        parentScene.removeSceneNode(removeNode, parentNode)
        sceneTree.refreshSceneTree()
    }

    override fun undo() {
        parentScene.addSceneNode(removeNode, parentNode)
        sceneTree.refreshSceneTree()
    }
}