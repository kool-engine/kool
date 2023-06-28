package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.data.MeshShapeData

class SetShapeAction(
    private val editedMeshComponent: MeshComponent,
    private val oldShape: MeshShapeData,
    private val newShape: MeshShapeData,
    private val replaceIndex: Int = editedMeshComponent.shapesState.indexOf(oldShape)
) : EditorAction {

    override fun doAction() {
        editedMeshComponent.shapesState[replaceIndex] = newShape
        editedMeshComponent.updateGeometry()
    }

    override fun undoAction() {
        editedMeshComponent.shapesState[replaceIndex] = oldShape
        editedMeshComponent.updateGeometry()
    }
}