package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.data.NodeId

class SetMaterialAction(
    nodeId: NodeId,
    val setMaterialModel: MaterialData?,
) : ComponentAction<MaterialComponent>(nodeId, MaterialComponent::class) {

    private val prevMaterial = component?.materialData

    override fun doAction() {
        component?.materialState?.set(setMaterialModel)
    }

    override fun undoAction() {
        component?.materialState?.set(prevMaterial)
    }
}