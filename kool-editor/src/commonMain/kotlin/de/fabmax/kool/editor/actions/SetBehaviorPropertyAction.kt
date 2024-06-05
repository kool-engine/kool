package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.BehaviorComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.PropertyValue

class SetBehaviorPropertyAction(
    entityId: EntityId,
    val propName: String,
    val undoValue: PropertyValue,
    val newValue: PropertyValue,
    val setPropertyBlock: (BehaviorComponent, PropertyValue) -> Unit
) : ComponentAction<BehaviorComponent>(entityId, BehaviorComponent::class) {

    override fun doAction() {
        component?.let { setPropertyBlock(it, newValue) }
    }

    override fun undoAction() {
        component?.let { setPropertyBlock(it, undoValue) }
    }

}