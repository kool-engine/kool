package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.data.PropertyValue
import de.fabmax.kool.editor.model.ScriptComponent

class SetScriptPropertyAction(
    val scriptComponent: ScriptComponent,
    val propName: String,
    val newValue: PropertyValue,
    val setPropertyBlock: (PropertyValue) -> Unit
) : EditorAction {

    private var isFirstApply = true
    private var undoValue: PropertyValue? = null

    override fun apply() {
        if (isFirstApply) {
            isFirstApply = false
            undoValue = scriptComponent.componentData.propertyValues[propName]
        }
        setPropertyBlock(newValue)
    }

    override fun undo() {
        undoValue?.let {
            setPropertyBlock(it)
        }
    }

}