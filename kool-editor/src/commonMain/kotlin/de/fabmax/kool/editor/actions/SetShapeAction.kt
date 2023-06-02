package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.data.MeshShapeData
import de.fabmax.kool.editor.model.SceneNodeModel

class SetShapeAction(
    private val nodeModel: SceneNodeModel,
    private val editedMeshComponent: MeshComponent,
    private val oldShape: MeshShapeData,
    private val newShape: MeshShapeData,
    private val replaceIndex: Int = editedMeshComponent.shapesState.indexOf(oldShape)
) : EditorAction {

    override fun apply() {
        editedMeshComponent.shapesState[replaceIndex] = newShape
        editedMeshComponent.updateGeometry()
    }

    override fun undo() {
        editedMeshComponent.shapesState[replaceIndex] = oldShape
        editedMeshComponent.updateGeometry()
    }
}