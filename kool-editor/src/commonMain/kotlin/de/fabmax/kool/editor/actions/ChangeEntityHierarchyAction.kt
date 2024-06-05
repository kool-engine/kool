package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.util.gameEntity

class ChangeEntityHierarchyAction(
    moveNodeModels: List<GameEntity>,
    val newParentId: EntityId,
    val insertionPos: GameEntity.InsertionPos
) : GameEntityAction(moveNodeModels) {

    private val undoInfos = mutableMapOf<EntityId, UndoInfo>()

    init {
        moveNodeModels.forEach { moveNodeModel ->
            val parent = checkNotNull(moveNodeModel.parent)
            val undoIdx = parent.children.indexOf(moveNodeModel)
            val undoPos = if (undoIdx > 0) {
                val after = parent.children[undoIdx - 1]
                GameEntity.InsertionPos.After(after.id)
            } else {
                val before = parent.children.getOrNull(undoIdx + 1)
                before?.let { GameEntity.InsertionPos.Before(it.id) } ?: GameEntity.InsertionPos.End
            }
            undoInfos[moveNodeModel.id] = UndoInfo(parent.id, undoPos)
        }
    }

    override fun doAction() {
        val newParent = newParentId.gameEntity ?: return
        var insertionPos = this.insertionPos

        gameEntities.forEach { nodeModel ->
            undoInfos[nodeModel.id]?.let { undoInfo ->
                val oldParent = undoInfo.parent.gameEntity
                if (oldParent != null) {
                    oldParent.removeChild(nodeModel)
                    newParent.addChild(nodeModel, insertionPos)
                    insertionPos = GameEntity.InsertionPos.After(nodeModel.id)
                }
            }
        }
        KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
    }

    override fun undoAction() {
        val oldParent = newParentId.gameEntity ?: return

        gameEntities.forEach { nodeModel ->
            undoInfos[nodeModel.id]?.let { undoInfo ->
                val newParent = undoInfo.parent.gameEntity
                if (newParent != null) {
                    oldParent.removeChild(nodeModel)
                    newParent.addChild(nodeModel, undoInfo.insertionPos)
                }
            }
        }
        KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
    }

    private class UndoInfo(val parent: EntityId, val insertionPos: GameEntity.InsertionPos)
}