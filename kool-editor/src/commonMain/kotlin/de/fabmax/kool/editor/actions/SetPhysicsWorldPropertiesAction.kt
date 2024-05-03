package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.PhysicsWorldComponent
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.data.PhysicsWorldProperties

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