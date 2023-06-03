package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.components.updateMaterial
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.data.MaterialShaderData

class UpdateMaterialAction(
    val materialData: MaterialData,
    val applyMaterial: MaterialShaderData,
    val undoMaterial: MaterialShaderData
) : EditorAction {
    override fun apply() {
        materialData.shaderDataState.set(applyMaterial)
        EditorState.projectModel.updateMaterial(materialData)
    }

    override fun undo() {
        materialData.shaderDataState.set(undoMaterial)
        EditorState.projectModel.updateMaterial(materialData)
    }
}