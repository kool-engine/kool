package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ModelComponent
import de.fabmax.kool.editor.data.ModelComponentData

class SetModelDataAction(
    component: ModelComponent,
    private val undoData: ModelComponentData,
    private val applyData: ModelComponentData
) : ComponentAction<ModelComponent>(component.gameEntity.id, ModelComponent::class) {

    override fun doAction() {
        component?.setPersistent(applyData)
    }

    override fun undoAction() {
        component?.setPersistent(undoData)
    }
}