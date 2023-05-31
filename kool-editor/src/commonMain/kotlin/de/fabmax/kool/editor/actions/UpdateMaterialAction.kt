package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.data.MaterialShaderData
import de.fabmax.kool.editor.model.updateMaterial

class UpdateMaterialAction(
    val materialData: MaterialData,
    val applyMaterial: MaterialShaderData,
    val undoMaterial: MaterialShaderData
) : EditorAction {
    override fun apply() {
        materialData.shaderData = applyMaterial
        EditorState.projectModel.updateMaterial(materialData)
    }

    override fun undo() {
        materialData.shaderData = undoMaterial
        EditorState.projectModel.updateMaterial(materialData)
    }
}