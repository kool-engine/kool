package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.toHierarchy
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.util.gameEntity
import de.fabmax.kool.util.launchOnMainThread

class ChangeEntityHierarchyAction(
    moveEntities: List<GameEntity>,
    val newParentId: EntityId,
    val insertionPos: GameEntity.InsertionPos
) : EditorAction {

    private val undoInfos = mutableMapOf<EntityId, UndoInfo>()
    private val moveHierarchy = moveEntities.toHierarchy().associateBy { it.entityData.id }

    init {
        moveHierarchy.keys.forEach { moveEntityId ->
            val gameEntity = checkNotNull(moveEntityId.gameEntity)
            val parent = checkNotNull(gameEntity.parent)
            val undoIdx = parent.children.indexOf(gameEntity)
            val undoPos = if (undoIdx > 0) {
                val after = parent.children[undoIdx - 1]
                GameEntity.InsertionPos.After(after.id)
            } else {
                val before = parent.children.getOrNull(undoIdx + 1)
                before?.let { GameEntity.InsertionPos.Before(it.id) } ?: GameEntity.InsertionPos.End
            }
            undoInfos[moveEntityId] = UndoInfo(parent.id, undoPos)
        }
    }

    override fun doAction() {
        launchOnMainThread {
            var insertionPos = this.insertionPos
            moveHierarchy.keys.forEach { moveEntityId ->
                val gameEntity = checkNotNull(moveEntityId.gameEntity)
                val hierarchy = checkNotNull(moveHierarchy[gameEntity.id])
                gameEntity.scene.removeGameEntity(gameEntity)

                hierarchy.entityData.parentId = newParentId
                gameEntity.scene.addGameEntities(hierarchy, insertionPos)
                insertionPos = GameEntity.InsertionPos.After(gameEntity.id)
            }
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }

    override fun undoAction() {
        launchOnMainThread {
            moveHierarchy.keys.forEach { moveEntityId ->
                val gameEntity = checkNotNull(moveEntityId.gameEntity)
                val hierarchy = checkNotNull(moveHierarchy[gameEntity.id])
                val undoInfo = checkNotNull(undoInfos[gameEntity.id])
                gameEntity.scene.removeGameEntity(gameEntity)

                hierarchy.entityData.parentId = undoInfo.parent
                gameEntity.scene.addGameEntities(hierarchy, undoInfo.insertionPos)
            }
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }

    private class UndoInfo(val parent: EntityId, val insertionPos: GameEntity.InsertionPos)
}