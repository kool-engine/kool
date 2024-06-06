package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.SceneComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.SceneComponentData

class SetScenePropertiesAction(
    sceneId: EntityId,
    val undoProperties: SceneComponentData,
    val applyProperties: SceneComponentData
) : ComponentAction<SceneComponent>(sceneId, SceneComponent::class) {

    override fun doAction() {
        component?.setPersistent(applyProperties)
    }

    override fun undoAction() {
        component?.setPersistent(undoProperties)
    }
}