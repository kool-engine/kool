package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.toHierarchy
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.util.gameEntity
import de.fabmax.kool.util.launchOnMainThread

class DeleteEntitiesAction(
    gameEntities: List<GameEntity>
) : EditorAction {

    private val hierarchy = gameEntities.toHierarchy()
    private val positions = mutableMapOf<EntityId, GameEntity.InsertionPos>()

    init {
        gameEntities.forEach { gameEntity ->
            val parent = gameEntity.parent!!
            val entityIdx = parent.children.indexOf(gameEntity)
            positions[gameEntity.id] = if (entityIdx > 0) {
                GameEntity.InsertionPos.After(parent.children[entityIdx - 1].id)
            } else {
                val before = parent.children.getOrNull(1)
                before?.let { GameEntity.InsertionPos.Before(it.id) } ?: GameEntity.InsertionPos.End
            }
        }
    }

    override fun doAction() {
        KoolEditor.instance.selectionOverlay.reduceSelection(hierarchy.mapNotNull { it.entityData.id.gameEntity })
        hierarchy.forEach { root ->
            val entity = root.entityData.id.gameEntity
            val scene = entity?.scene
            entity?.let { scene?.removeGameEntity(it) }
        }
        refreshComponentViews()
    }

    override fun undoAction() {
        launchOnMainThread {
            hierarchy.forEach {
                val scene = it.entityData.parentId?.gameEntity?.scene
                scene?.addGameEntities(it, positions[it.entityData.id] ?: GameEntity.InsertionPos.End)
            }
            refreshComponentViews()
        }
    }
}

fun deleteNode(node: GameEntity?) {
    node?.let { DeleteEntitiesAction(listOf(it)).apply() }
}
