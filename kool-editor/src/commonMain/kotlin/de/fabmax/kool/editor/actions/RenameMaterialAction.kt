package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.data.MaterialData

class RenameMaterialAction(
    val materialData: MaterialData,
    val applyName: String,
    val undoName: String
) : EditorAction {

    override fun apply() {
        materialData.nameState.set(applyName)
        // re-add material to keep correct order
        EditorState.projectModel.removeMaterial(materialData)
        EditorState.projectModel.addMaterial(materialData)
    }

    override fun undo() {
        materialData.nameState.set(undoName)
        // re-add material to keep correct order
        EditorState.projectModel.removeMaterial(materialData)
        EditorState.projectModel.addMaterial(materialData)
    }
}