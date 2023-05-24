package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.ui.SceneObjectTree
import de.fabmax.kool.util.launchOnMainThread

class AddNodeAction(
    private val addNodeModel: SceneNodeModel,
    private val parentNodeModel: EditorNodeModel,
    private val parentSceneModel: SceneModel,
    private val sceneTree: SceneObjectTree
) : EditorAction {

    override fun apply() {
        launchOnMainThread {
            val needsInit = !addNodeModel.isCreated
            if (needsInit) {
                addNodeModel.createComponents()
            }

            parentSceneModel.addSceneNode(addNodeModel, parentNodeModel)
            sceneTree.refreshSceneTree()

            if (needsInit) {
                addNodeModel.initComponents()
            }
        }
    }

    override fun undo() {
        if (EditorState.selectedNode.value == addNodeModel) {
            EditorState.selectedNode.set(null)
        }
        parentSceneModel.removeSceneNode(addNodeModel, parentNodeModel)
        sceneTree.refreshSceneTree()
    }
}