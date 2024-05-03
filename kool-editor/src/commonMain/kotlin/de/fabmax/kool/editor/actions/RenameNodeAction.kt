package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.util.nodeModel

class RenameNodeAction(
    val nodeId: NodeId,
    val applyName: String,
    val undoName: String
) : EditorAction {

    private val nodeModel: NodeModel? get() = nodeId.nodeModel

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