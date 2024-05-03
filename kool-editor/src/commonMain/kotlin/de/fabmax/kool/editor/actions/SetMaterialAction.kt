package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.MaterialData

class SetMaterialAction(
    component: MaterialComponent,
    val setMaterialModel: MaterialData?,
) : ComponentAction<MaterialComponent>(component) {

    private val component: MaterialComponent? get() = nodeModel?.getComponent()
    private val prevMaterial = component.materialData

    override fun doAction() {
        component?.materialState?.set(setMaterialModel)
    }

    override fun undoAction() {
        component?.materialState?.set(prevMaterial)
    }
}