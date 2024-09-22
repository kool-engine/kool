package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.BehaviorComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.PropertyValue
import de.fabmax.kool.editor.util.gameEntity

class SetBehaviorPropertyAction(
    val entityId: EntityId,
    val propName: String,
    val behaviorClassName: String,
    val undoValue: PropertyValue,
    val newValue: PropertyValue,
    val setPropertyBlock: (BehaviorComponent, PropertyValue) -> Unit
) : EditorAction {

    val gameEntity: GameEntity? get() = entityId.gameEntity
    val component: BehaviorComponent? get() {
        return gameEntity?.components?.first { it is BehaviorComponent && it.data.behaviorClassName == behaviorClassName } as BehaviorComponent?
    }

    override fun doAction() {
        component?.let { setPropertyBlock(it, newValue) }
    }

    override fun undoAction() {
        component?.let { setPropertyBlock(it, undoValue) }
    }

}