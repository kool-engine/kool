package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.CharacterControllerComponent
import de.fabmax.kool.editor.data.CharacterControllerComponentProperties
import de.fabmax.kool.editor.data.NodeId

class SetCharControllerPropertiesAction(
    nodeId: NodeId,
    private val oldProps: CharacterControllerComponentProperties,
    private val newProps: CharacterControllerComponentProperties
) : ComponentAction<CharacterControllerComponent>(nodeId, CharacterControllerComponent::class) {

    override fun doAction() {
        component?.charControllerState?.set(newProps)
    }

    override fun undoAction() {
        component?.charControllerState?.set(oldProps)
    }
}