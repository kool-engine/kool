package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.RigidActorComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.RigidActorProperties

class SetRigidBodyPropertiesAction(
    entityId: EntityId,
    private val oldProps: RigidActorProperties,
    private val newProps: RigidActorProperties
) : ComponentAction<RigidActorComponent>(entityId, RigidActorComponent::class) {

    override fun doAction() {
        component?.actorState?.set(newProps)
    }

    override fun undoAction() {
        component?.actorState?.set(oldProps)
    }
}