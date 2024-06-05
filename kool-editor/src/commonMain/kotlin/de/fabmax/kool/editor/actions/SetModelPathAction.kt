package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ModelComponent
import de.fabmax.kool.editor.data.EntityId

class SetModelPathAction(
    entityId: EntityId,
    private val newPath: String,
) : ComponentAction<ModelComponent>(entityId, ModelComponent::class) {

    private val oldPath = component?.modelPathState?.value

    override fun doAction() {
        component?.modelPathState?.set(newPath)
    }

    override fun undoAction() {
        oldPath?.let { component?.modelPathState?.set(it) }
    }
}