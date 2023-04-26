package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.model.MMesh
import de.fabmax.kool.editor.model.MMeshShape

class SetShapeAction(
    private val editedMeshModel: MMesh,
    private val oldShape: MMeshShape,
    private val newShape: MMeshShape
) : EditorAction {

    override fun apply() {
        editedMeshModel.shapeMutableState.set(newShape)
        editedMeshModel.generateMeshType()
    }

    override fun undo() {
        editedMeshModel.shapeMutableState.set(oldShape)
        editedMeshModel.generateMeshType()
    }
}