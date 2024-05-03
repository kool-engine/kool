package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.data.MeshShapeData

class SetMeshShapeAction(
    component: MeshComponent,
    private val oldShape: MeshShapeData,
    private val newShape: MeshShapeData,
    private val replaceIndex: Int = component.shapesState.indexOf(oldShape)
) : ComponentAction<MeshComponent>(component.nodeModel.nodeId, MeshComponent::class) {

    override fun doAction() {
        component?.shapesState?.set(replaceIndex, newShape)
        component?.updateGeometry()
    }

    override fun undoAction() {
        component?.shapesState?.set(replaceIndex, oldShape)
        component?.updateGeometry()
    }
}