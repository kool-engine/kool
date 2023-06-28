package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.data.MaterialData

class DeleteMaterialAction(
    val materialToDelete: MaterialData
) : EditorAction {
    override fun doAction() {
        // todo: collect material users and clear their material
        EditorState.projectModel.removeMaterial(materialToDelete)
    }

    override fun undoAction() {
        EditorState.projectModel.addMaterial(materialToDelete)
    }
}