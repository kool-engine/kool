package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.BehaviorComponent
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.data.PropertyValue

class SetBehaviorPropertyAction(
    nodeId: NodeId,
    val propName: String,
    val undoValue: PropertyValue,
    val newValue: PropertyValue,
    val setPropertyBlock: (BehaviorComponent, PropertyValue) -> Unit
) : ComponentAction<BehaviorComponent>(nodeId, BehaviorComponent::class) {

    override fun doAction() {
        component?.let { setPropertyBlock(it, newValue) }
    }

    override fun undoAction() {
        component?.let { setPropertyBlock(it, undoValue) }
    }

}