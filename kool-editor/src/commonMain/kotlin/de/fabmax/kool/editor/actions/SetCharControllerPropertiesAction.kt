package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.CharacterControllerComponent
import de.fabmax.kool.editor.data.CharacterControllerComponentData
import de.fabmax.kool.editor.data.EntityId

class SetCharControllerPropertiesAction(
    entityId: EntityId,
    private val oldProps: CharacterControllerComponentData,
    private val newProps: CharacterControllerComponentData
) : ComponentAction<CharacterControllerComponent>(entityId, CharacterControllerComponent::class) {

    override fun doAction() {
        component?.setPersistent(newProps)
    }

    override fun undoAction() {
        component?.setPersistent(oldProps)
    }
}