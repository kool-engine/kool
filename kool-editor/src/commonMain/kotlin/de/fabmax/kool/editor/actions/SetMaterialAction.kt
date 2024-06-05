package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.MaterialData

class SetMaterialAction(
    entityId: EntityId,
    val setMaterialModel: MaterialData?,
) : ComponentAction<MaterialComponent>(entityId, MaterialComponent::class) {

    private val prevMaterial = component?.materialData

    override fun doAction() {
        component?.materialState?.set(setMaterialModel)
    }

    override fun undoAction() {
        component?.materialState?.set(prevMaterial)
    }
}