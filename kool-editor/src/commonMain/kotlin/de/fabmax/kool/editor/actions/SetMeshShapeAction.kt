package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.data.ShapeData

class SetMeshShapeAction(
    component: MeshComponent,
    private val oldShape: ShapeData,
    private val newShape: ShapeData,
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