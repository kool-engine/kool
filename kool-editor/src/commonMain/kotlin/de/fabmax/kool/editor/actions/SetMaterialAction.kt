package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.MaterialData

class SetMaterialAction(
    val materialComponent: MaterialComponent,
    val setMaterialModel: MaterialData?,
) : EditorAction {

    private val prevMaterial = materialComponent.materialData

    override fun apply() {
        materialComponent.materialState.set(setMaterialModel)
    }

    override fun undo() {
        materialComponent.materialState.set(prevMaterial)
    }
}