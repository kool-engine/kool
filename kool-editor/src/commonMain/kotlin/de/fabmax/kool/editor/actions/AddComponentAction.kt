package de.fabmax.kool.editor.actions

import de.fabmax.kool.ApplicationScope
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.GameEntityComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.util.gameEntity
import de.fabmax.kool.util.Frontend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddComponentAction(
    val entityId: EntityId,
    val component: GameEntityComponent
) : EditorAction {

    private val gameEntity: GameEntity? get() = entityId.gameEntity

    // fixme: component is not recreated on undo / redo, therefore redo can fail

    override fun doAction() {
        ApplicationScope.launch(Dispatchers.Frontend) {
            gameEntity?.addComponentLifecycleAware(component)
            refreshComponentViews()
        }
    }

    override fun undoAction() {
        gameEntity?.removeComponent(component)
        refreshComponentViews()
    }
}