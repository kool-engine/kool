package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.data.MeshShapeData
import de.fabmax.kool.editor.model.MeshComponent
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.util.logE

class SetShapeAction(
    private val nodeModel: SceneNodeModel,
    private val editedMeshComponent: MeshComponent,
    private val oldShape: MeshShapeData,
    private val newShape: MeshShapeData
) : EditorAction {

    override fun apply() {
        replaceShape(oldShape, newShape)
    }

    override fun undo() {
        replaceShape(newShape, oldShape)
    }

    private fun replaceShape(old: MeshShapeData, new: MeshShapeData) {
        val oldIdx = editedMeshComponent.shapesState.indexOf(old)
        if (oldIdx < 0) {
            logE { "Replace shape not found when trying to replace ${old.name} by ${new.name} in mesh ${nodeModel.name}" }
            editedMeshComponent.shapesState.add(new)
        } else {
            editedMeshComponent.shapesState[oldIdx] = new
        }
        nodeModel.regenerateGeometry(editedMeshComponent)
    }
}