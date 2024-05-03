package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ModelComponent
import de.fabmax.kool.editor.data.NodeId

class SetModelSceneAction(
    nodeId: NodeId,
    private val newScene: Int,
) : ComponentAction<ModelComponent>(nodeId, ModelComponent::class) {

    private val oldScene = component?.sceneIndexState?.value

    override fun doAction() {
        component?.sceneIndexState?.set(newScene)
    }

    override fun undoAction() {
        oldScene?.let { component?.sceneIndexState?.set(it) }
    }
}