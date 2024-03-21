package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.components.updateMaterial
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.data.MaterialShaderData

class UpdateMaterialAction(
    val materialData: MaterialData,
    val applyMaterial: MaterialShaderData,
    val undoMaterial: MaterialShaderData
) : EditorAction {
    override fun doAction() {
        materialData.shaderDataState.set(applyMaterial)
        KoolEditor.instance.projectModel.updateMaterial(materialData)
    }

    override fun undoAction() {
        materialData.shaderDataState.set(undoMaterial)
        KoolEditor.instance.projectModel.updateMaterial(materialData)
    }
}