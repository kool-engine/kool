package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.TransformComponent
import de.fabmax.kool.editor.data.TransformComponentData

class SetTransformAction(
    component: TransformComponent,
    private val undoData: TransformComponentData,
    private val applyData: TransformComponentData
) : ComponentAction<TransformComponent>(component.gameEntity.id, TransformComponent::class) {

    override fun doAction() {
        component?.let {
            it.setPersistent(applyData)
            it.updateTransformRecursive()
        }
    }
    override fun undoAction() {
        component?.let {
            it.setPersistent(undoData)
            it.updateTransformRecursive()
        }
    }
}
