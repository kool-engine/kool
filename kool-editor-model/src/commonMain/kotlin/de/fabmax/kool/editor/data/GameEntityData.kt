package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
class GameEntityData(
    var name: String,
    var id: EntityId,
    var parentId: EntityId? = null,
    var order: Int = 0,
    var isVisible: Boolean = true
) {
    val components: MutableList<ComponentData> = mutableListOf()
}

@Serializable
@JvmInline
value class EntityId(val value: Long)