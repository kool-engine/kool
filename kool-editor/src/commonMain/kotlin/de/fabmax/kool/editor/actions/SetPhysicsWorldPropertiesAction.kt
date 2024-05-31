package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.PhysicsWorldComponent
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.data.PhysicsWorldProperties
import de.fabmax.kool.math.Vec3f

class SetPhysicsWorldPropertiesAction(
    nodeId: NodeId,
    private val oldProps: PhysicsWorldProperties,
    private val newProps: PhysicsWorldProperties
) : ComponentAction<PhysicsWorldComponent>(nodeId, PhysicsWorldComponent::class) {

    override fun doAction() {
        component?.physicsWorldState?.set(newProps)
    }

    override fun undoAction() {
        component?.physicsWorldState?.set(oldProps)
    }
}

class SetPhysicsWorldGravityAction(
    nodeId: NodeId,
    private val oldGravity: Vec3f,
    private val newGravity: Vec3f
) : ComponentAction<PhysicsWorldComponent>(nodeId, PhysicsWorldComponent::class) {

    override fun doAction() {
        component?.gravityState?.set(newGravity)
    }

    override fun undoAction() {
        component?.gravityState?.set(oldGravity)
    }
}