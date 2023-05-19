package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.data.MeshComponentData
import de.fabmax.kool.editor.data.MeshShapeData
import de.fabmax.kool.editor.model.MSceneNode

class SetShapeAction(
    private val nodeModel: MSceneNode,
    private val editedMeshData: MeshComponentData,
    private val oldShape: MeshShapeData,
    private val newShape: MeshShapeData
) : EditorAction {

    override fun apply() {
        editedMeshData.shape = newShape
        nodeModel.regenerateMesh()
        nodeModel.markModified()
    }

    override fun undo() {
        editedMeshData.shape = oldShape
        nodeModel.regenerateMesh()
        nodeModel.markModified()
    }
}