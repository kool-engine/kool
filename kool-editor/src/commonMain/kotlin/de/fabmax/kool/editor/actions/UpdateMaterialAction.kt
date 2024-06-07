package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.MaterialComponentData

class UpdateMaterialAction(
    component: MaterialComponent,
    val oldData: MaterialComponentData,
    val newData: MaterialComponentData
) : ComponentAction<MaterialComponent>(component.gameEntity.id, MaterialComponent::class) {

    override fun doAction() {
        component?.setPersistent(newData)
    }

    override fun undoAction() {
        component?.setPersistent(oldData)
    }
}