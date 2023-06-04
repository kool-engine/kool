package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.model.EditorNodeModel

class RenameNodeAction(
    val nodeModel: EditorNodeModel,
    val applyName: String,
    val undoName: String
) : EditorAction {
    override fun apply() {
        nodeModel.nameState.set(applyName)
        KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
    }

    override fun undo() {
        nodeModel.nameState.set(undoName)
        KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
    }
}