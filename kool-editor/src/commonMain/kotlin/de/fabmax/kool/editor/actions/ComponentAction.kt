package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.GameEntityComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.util.gameEntity
import kotlin.reflect.KClass

abstract class ComponentAction<T: GameEntityComponent>(
    val entityId: EntityId,
    val componentType: KClass<T>
) : EditorAction {
    val gameEntity: GameEntity? get() = entityId.gameEntity
    val component: T? get() {
        @Suppress("UNCHECKED_CAST")
        return gameEntity?.components?.first { it::class == componentType } as T?
    }
}