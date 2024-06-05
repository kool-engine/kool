package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ModelComponent
import de.fabmax.kool.editor.data.EntityId

class SetModelSceneAction(
    entityId: EntityId,
    private val newScene: Int,
) : ComponentAction<ModelComponent>(entityId, ModelComponent::class) {

    private val oldScene = component?.sceneIndexState?.value

    override fun doAction() {
        component?.sceneIndexState?.set(newScene)
    }

    override fun undoAction() {
        oldScene?.let { component?.sceneIndexState?.set(it) }
    }
}