package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.data.ShapeData

class SetMeshShapeAction(
    component: MeshComponent,
    private val oldShape: ShapeData,
    private val newShape: ShapeData,
    private val replaceIndex: Int = 0
) : ComponentAction<MeshComponent>(component.gameEntity.id, MeshComponent::class) {

    override fun doAction() {
        component?.let {
            val applyShapes = it.data.shapes.toMutableList()
            applyShapes[replaceIndex] = newShape
            it.setPersistent(it.data.copy(shapes = applyShapes))
        }
    }

    override fun undoAction() {
        component?.let {
            val undoShapes = it.data.shapes.toMutableList()
            undoShapes[replaceIndex] = oldShape
            it.setPersistent(it.data.copy(shapes = undoShapes))
        }
    }
}