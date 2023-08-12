package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.BehaviorComponent
import de.fabmax.kool.editor.data.PropertyValue

class SetBehaviorPropertyAction(
    val behaviorComponent: BehaviorComponent,
    val propName: String,
    val undoValue: PropertyValue,
    val newValue: PropertyValue,
    val setPropertyBlock: (PropertyValue) -> Unit
) : EditorAction {

    override fun doAction() {
        setPropertyBlock(newValue)
    }

    override fun undoAction() {
        setPropertyBlock(undoValue)
    }

}