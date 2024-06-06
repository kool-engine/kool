package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.MaterialData

class SetMaterialAction(
    entityId: EntityId,
    setMaterial: MaterialData?,
) : ComponentAction<MaterialComponent>(entityId, MaterialComponent::class) {

    private val applyMaterialId = setMaterial?.id ?: EntityId(0L)
    private val undoMaterialId = component?.material?.id ?: EntityId(0L)

    override fun doAction() {
        component?.let { it.setPersistent(it.data.copy(materialId = applyMaterialId)) }
    }

    override fun undoAction() {
        component?.let { it.setPersistent(it.data.copy(materialId = undoMaterialId)) }
    }
}