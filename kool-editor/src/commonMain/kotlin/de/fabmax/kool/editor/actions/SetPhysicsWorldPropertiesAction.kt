package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.PhysicsWorldComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.PhysicsWorldComponentData
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.math.Vec3f

class SetPhysicsWorldPropertiesAction(
    entityId: EntityId,
    private val oldProps: PhysicsWorldComponentData,
    private val newProps: PhysicsWorldComponentData
) : ComponentAction<PhysicsWorldComponent>(entityId, PhysicsWorldComponent::class) {

    override fun doAction() {
        component?.setPersistent(newProps)
    }

    override fun undoAction() {
        component?.setPersistent(oldProps)
    }
}

class SetPhysicsWorldGravityAction(
    entityId: EntityId,
    private val oldGravity: Vec3f,
    private val newGravity: Vec3f
) : ComponentAction<PhysicsWorldComponent>(entityId, PhysicsWorldComponent::class) {

    override fun doAction() {
        component?.let { it.setPersistent(it.data.copy(gravity = Vec3Data(newGravity))) }
    }

    override fun undoAction() {
        component?.let { it.setPersistent(it.data.copy(gravity = Vec3Data(oldGravity))) }
    }
}