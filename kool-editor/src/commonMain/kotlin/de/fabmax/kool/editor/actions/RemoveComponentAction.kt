package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.EditorModelComponent
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.util.nodeModel

class RemoveComponentAction(
    val nodeId: NodeId,
    val component: EditorModelComponent
) : EditorAction {

    private val nodeModel: NodeModel? get() = nodeId.nodeModel

    // fixme: component is not recreated on undo / redo, therefore redo can fail

    override fun doAction() {
        nodeModel?.removeComponent(component)
        refreshComponentViews()
    }

    override fun undoAction() {
        nodeModel?.addComponent(component)
        refreshComponentViews()
    }
}