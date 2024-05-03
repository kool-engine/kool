package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ModelComponent
import de.fabmax.kool.editor.data.NodeId

class SetModelAnimationAction(
    nodeId: NodeId,
    private val newAnimation: Int,
) : ComponentAction<ModelComponent>(nodeId, ModelComponent::class) {

    private val oldAnimation = component?.animationIndexState?.value

    override fun doAction() {
        component?.animationIndexState?.set(newAnimation)
    }

    override fun undoAction() {
        oldAnimation?.let { component?.animationIndexState?.set(it) }
    }
}