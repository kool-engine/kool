package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.GameEntityDataComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.util.gameEntity
import de.fabmax.kool.util.launchOnMainThread

class RemoveComponentAction(
    val entityId: EntityId,
    component: GameEntityDataComponent<*>
) : EditorAction {

    private val componentType = component.componentType
    private val componentInfo = component.componentInfo
    private val gameEntity: GameEntity? get() = entityId.gameEntity

    override fun doAction() {
        val entity = gameEntity ?: return
        entity.components.firstOrNull { it.componentType == componentType }?.let { rem ->
            entity.removeComponent(rem)
            refreshComponentViews()
        }
    }

    override fun undoAction() {
        val entity = gameEntity ?: return
        launchOnMainThread {
            val component = entity.createDataComponent(componentInfo)
            entity.addComponentLifecycleAware(component)
            refreshComponentViews()
        }
    }
}