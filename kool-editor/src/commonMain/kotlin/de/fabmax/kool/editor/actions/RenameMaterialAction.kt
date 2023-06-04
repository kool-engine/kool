package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.data.MaterialData

class RenameMaterialAction(
    val materialData: MaterialData,
    val applyName: String,
    val undoName: String
) : EditorAction {
    override fun apply() = materialData.nameState.set(applyName)
    override fun undo() = materialData.nameState.set(undoName)
}