package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.util.launchOnMainThread

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
        launchOnMainThread {
            var insertionPos = this.insertionPos
            gameEntities.forEach { gameEntity ->
                gameEntity.scene.removeGameEntity(gameEntity)

                val entityData = gameEntity.entityData
                entityData.parentId = newParentId
                gameEntity.scene.addGameEntity(entityData, insertionPos)
                insertionPos = GameEntity.InsertionPos.After(gameEntity.id)
            }
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }

    override fun undoAction() {
        launchOnMainThread {
            gameEntities.forEach { gameEntity ->
                undoInfos[gameEntity.id]?.let { undoInfo ->
                    gameEntity.scene.removeGameEntity(gameEntity)

                    val entityData = gameEntity.entityData
                    entityData.parentId = undoInfo.parent
                    gameEntity.scene.addGameEntity(entityData, undoInfo.insertionPos)
                }
            }
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }

    private class UndoInfo(val parent: EntityId, val insertionPos: GameEntity.InsertionPos)
}