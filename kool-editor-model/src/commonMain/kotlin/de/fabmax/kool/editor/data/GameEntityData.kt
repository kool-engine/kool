package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
data class GameEntityData(
    val id: EntityId,
    var name: String,
    var parentId: EntityId?,
    var order: Int = 0,
    var isVisible: Boolean = true,
    var drawGroupId: Int = 0
) {
    val components: MutableList<ComponentInfo<*>> = mutableListOf()
}

@Serializable
data class ComponentInfo<T: ComponentData>(var data: T, var displayOrder: Int = -1)

@Serializable
@JvmInline
value class EntityId(val value: Long)
