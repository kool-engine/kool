package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
class GameEntityData(
    var name: String,
    var id: EntityId,
    var isVisible: Boolean = true
) {
    val components: MutableList<ComponentData> = mutableListOf()
    val childEntityIds: MutableList<EntityId> = mutableListOf()
}

@Serializable
@JvmInline
value class EntityId(val value: Long)