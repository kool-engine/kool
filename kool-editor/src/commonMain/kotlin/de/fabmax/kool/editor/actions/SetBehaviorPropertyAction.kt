package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.BehaviorComponent
import de.fabmax.kool.editor.data.PropertyValue

class SetBehaviorPropertyAction(
    component: BehaviorComponent,
    val propName: String,
    val undoValue: PropertyValue,
    val newValue: PropertyValue,
    val setPropertyBlock: (BehaviorComponent, PropertyValue) -> Unit
) : ComponentAction<BehaviorComponent>(component) {

    private val component: BehaviorComponent? get() = nodeModel?.getComponent()

    override fun doAction() {
        component?.let { setPropertyBlock(it, newValue) }
    }

    override fun undoAction() {
        component?.let { setPropertyBlock(it, undoValue) }
    }

}