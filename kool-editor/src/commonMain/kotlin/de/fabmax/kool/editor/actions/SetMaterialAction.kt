package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.components.MaterialReferenceComponent
import de.fabmax.kool.editor.data.EntityId

class SetMaterialAction(
    component: MaterialReferenceComponent,
    applyMaterial: MaterialComponent?,
) : ComponentAction<MaterialReferenceComponent>(component.gameEntity.id, MaterialReferenceComponent::class) {

    private val applyMaterialId = applyMaterial?.id ?: EntityId.NULL
    private val undoMaterialId = component.material?.id ?: EntityId.NULL

    override fun doAction() {
        component?.let { it.setPersistent(it.data.copy(materialId = applyMaterialId)) }
    }

    override fun undoAction() {
        component?.let { it.setPersistent(it.data.copy(materialId = undoMaterialId)) }
    }
}