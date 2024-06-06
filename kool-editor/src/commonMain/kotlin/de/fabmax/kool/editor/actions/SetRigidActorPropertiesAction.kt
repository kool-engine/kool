package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.RigidActorComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.RigidActorComponentData

class SetRigidActorPropertiesAction(
    entityId: EntityId,
    private val oldProps: RigidActorComponentData,
    private val newProps: RigidActorComponentData
) : ComponentAction<RigidActorComponent>(entityId, RigidActorComponent::class) {

    override fun doAction() {
        component?.setPersistent(newProps)
    }

    override fun undoAction() {
        component?.setPersistent(oldProps)
    }
}