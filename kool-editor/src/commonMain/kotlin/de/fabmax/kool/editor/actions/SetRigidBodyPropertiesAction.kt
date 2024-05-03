package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.RigidBodyComponent
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.data.RigidBodyProperties

class SetRigidBodyPropertiesAction(
    nodeId: NodeId,
    private val oldProps: RigidBodyProperties,
    private val newProps: RigidBodyProperties
) : ComponentAction<RigidBodyComponent>(nodeId, RigidBodyComponent::class) {

    override fun doAction() {
        component?.bodyState?.set(newProps)
    }

    override fun undoAction() {
        component?.bodyState?.set(oldProps)
    }
}