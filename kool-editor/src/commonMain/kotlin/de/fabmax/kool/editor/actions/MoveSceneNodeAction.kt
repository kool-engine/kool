package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneNodeModel

class MoveSceneNodeAction(
    val moveNode: SceneNodeModel,
    val newParent: NodeModel
) : EditorAction {

    val oldParent = moveNode.parent

    override fun doAction() {
        oldParent.removeChild(moveNode)
        newParent.addChild(moveNode)
        KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
    }

    override fun undoAction() {
        newParent.removeChild(moveNode)
        oldParent.addChild(moveNode)
        KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
    }
}