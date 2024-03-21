package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.data.MaterialData

class DeleteMaterialAction(
    val materialToDelete: MaterialData
) : EditorAction {
    override fun doAction() {
        // todo: collect material users and clear their material
        KoolEditor.instance.projectModel.removeMaterial(materialToDelete)
    }

    override fun undoAction() {
        KoolEditor.instance.projectModel.addMaterial(materialToDelete)
    }
}