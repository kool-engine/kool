package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.api.GameEntity

class SetDrawGroupAction(
    entities: List<GameEntity>,
    val applyGroup: Int
) : GameEntityAction(entities) {

    private val undoGroups = entities.associate { it.id to it.entityData.settings.drawGroupId }

    override fun doAction() {
        gameEntities.forEach { it.setDrawGroup(applyGroup) }
    }

    override fun undoAction() {
        gameEntities.forEach { it.setDrawGroup(undoGroups[it.id] ?: 0) }
    }

    private fun GameEntity.setDrawGroup(group: Int) {
        setPersistent(settings.copy(drawGroupId = group))
    }
}