package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.api.GameEntity

class SetVisibilityAction(entities: List<GameEntity>, val visible: Boolean) : GameEntityAction(entities) {

    private val undoVisibilities = entities.associate { it.id to it.isVisible }

    constructor(gameEntity: GameEntity, visible: Boolean): this(listOf(gameEntity), visible)

    override fun doAction() {
        gameEntities.forEach { it.isVisible = visible }
        refreshComponentViews()
    }

    override fun undoAction() {
        gameEntities.forEach {
            undoVisibilities[it.id]?.let { undoState -> it.isVisible = undoState }
        }
        refreshComponentViews()
    }
}