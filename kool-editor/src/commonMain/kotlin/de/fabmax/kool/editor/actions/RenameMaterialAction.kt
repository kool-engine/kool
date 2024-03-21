package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.data.MaterialData

class RenameMaterialAction(
    val materialData: MaterialData,
    val applyName: String,
    val undoName: String
) : EditorAction {

    override fun doAction() {
        materialData.nameState.set(applyName)
        // re-add material to keep correct order
        KoolEditor.instance.projectModel.removeMaterial(materialData)
        KoolEditor.instance.projectModel.addMaterial(materialData)
    }

    override fun undoAction() {
        materialData.nameState.set(undoName)
        // re-add material to keep correct order
        KoolEditor.instance.projectModel.removeMaterial(materialData)
        KoolEditor.instance.projectModel.addMaterial(materialData)
    }
}