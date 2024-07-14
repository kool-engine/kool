package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
data class GameEntityData(
    val id: EntityId,
    var parentId: EntityId,
    var settings: GameEntitySettings,
    var order: Int = 0,
) {
    val components: MutableList<ComponentInfo<*>> = mutableListOf()

    init {
        check(id != parentId) { "Entity can not be its own parent" }
    }
}

@Serializable
data class GameEntitySettings(val name: String, val isVisible: Boolean = true, val drawGroupId: Int = 0)

@Serializable
data class ComponentInfo<T: ComponentData>(var data: T, var displayOrder: Int = -1)

@Serializable
@JvmInline
value class EntityId(val value: Long) {
    companion object {
        val NULL = EntityId(0L)
    }
}
