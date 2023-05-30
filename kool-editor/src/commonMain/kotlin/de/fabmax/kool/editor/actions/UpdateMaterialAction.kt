package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.model.MaterialModel
import de.fabmax.kool.editor.model.updateMaterial

class UpdateMaterialAction(
    val materialModel: MaterialModel,
    val applyMaterial: MaterialData,
    val undoMaterial: MaterialData
) : EditorAction {
    override fun apply() {
        materialModel.materialState.set(applyMaterial)
        EditorState.projectModel.updateMaterial(materialModel)
    }

    override fun undo() {
        materialModel.materialState.set(undoMaterial)
        EditorState.projectModel.updateMaterial(materialModel)
    }
}