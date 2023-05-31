package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.data.PropertyValue
import de.fabmax.kool.editor.model.ScriptComponent

class SetScriptPropertyAction(
    val scriptComponent: ScriptComponent,
    val propName: String,
    val undoValue: PropertyValue,
    val newValue: PropertyValue,
    val setPropertyBlock: (PropertyValue) -> Unit
) : EditorAction {

    override fun apply() {
        setPropertyBlock(newValue)
    }

    override fun undo() {
        setPropertyBlock(undoValue)
    }

}