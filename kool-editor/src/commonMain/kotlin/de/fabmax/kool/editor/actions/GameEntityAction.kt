package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.util.gameEntity

abstract class GameEntityAction(gameEntities: List<GameEntity>): EditorAction {
    private val entityIds = gameEntities.map { it.id }

    val gameEntities: List<GameEntity> get() = entityIds.mapNotNull { it.gameEntity }
    val gameEntity: GameEntity? get() = entityIds.firstOrNull()?.gameEntity
}
