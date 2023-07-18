package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.util.launchOnMainThread

class AddNodeAction(
    private val addNodeModel: SceneNodeModel
) : EditorAction {

    override fun doAction() {
        launchOnMainThread {
            addNodeModel.sceneModel.addSceneNode(addNodeModel)
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }

    override fun undoAction() {
        if (addNodeModel in EditorState.selection) {
            EditorState.selection -= addNodeModel
        }
        addNodeModel.sceneModel.removeSceneNode(addNodeModel)
        KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
    }
}