package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.GameEntity

class SetVisibilityAction(nodes: List<GameEntity>, val visible: Boolean) : GameEntityAction(nodes) {

    private val undoVisibilities = nodes.associate { it.entityId to it.isVisibleState.value }

    constructor(gameEntity: GameEntity, visible: Boolean): this(listOf(gameEntity), visible)

    override fun doAction() {
        gameEntities.forEach { it.isVisibleState.set(visible) }
        KoolEditor.instance.sceneObjectsOverlay.updateOverlayObjects()
    }

    override fun undoAction() {
        gameEntities.forEach {
            undoVisibilities[it.entityId]?.let { undoState -> it.isVisibleState.set(undoState) }
        }
        KoolEditor.instance.sceneObjectsOverlay.updateOverlayObjects()
    }
}