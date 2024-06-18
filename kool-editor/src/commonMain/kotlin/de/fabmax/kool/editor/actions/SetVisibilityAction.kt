package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.api.GameEntity

fun SetVisibilityAction(gameEntity: GameEntity, visible: Boolean) = SetVisibilityAction(listOf(gameEntity), visible)

class SetVisibilityAction(entities: List<GameEntity>, val visible: Boolean) : GameEntityAction(entities) {
    private val undoVisibilities = entities.associate { it.id to it.isVisible }

    override fun doAction() {
        gameEntities.forEach {
            it.setPersistent(it.settings.copy(isVisible = visible))
        }
        refreshComponentViews()
    }

    override fun undoAction() {
        gameEntities.forEach {
            undoVisibilities[it.id]?.let { undoState ->
                it.setPersistent(it.settings.copy(isVisible = undoState))
            }
        }
        refreshComponentViews()
    }
}