package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ModelComponent
import de.fabmax.kool.editor.data.NodeId

class SetModelPathAction(
    nodeId: NodeId,
    private val newPath: String,
) : ComponentAction<ModelComponent>(nodeId, ModelComponent::class) {

    private val oldPath = component?.modelPathState?.value

    override fun doAction() {
        component?.modelPathState?.set(newPath)
    }

    override fun undoAction() {
        oldPath?.let { component?.modelPathState?.set(it) }
    }
}