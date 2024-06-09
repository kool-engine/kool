package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.util.gameEntity

class RenameEntityAction(
    val entityId: EntityId,
    val applyName: String,
    val undoName: String
) : EditorAction {

    private val gameEntity: GameEntity? get() = entityId.gameEntity

    override fun doAction() {
        gameEntity?.let {
            it.name = applyName
            it.entityData.name = applyName
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }

    override fun undoAction() {
        gameEntity?.let {
            it.name = undoName
            it.entityData.name = undoName
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }
}