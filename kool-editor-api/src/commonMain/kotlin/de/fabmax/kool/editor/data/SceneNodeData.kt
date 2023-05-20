package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class SceneNodeData(var name: String, val nodeId: Long) {
    val components: MutableList<ComponentData> = mutableListOf()
    val childNodeIds: MutableSet<Long> = mutableSetOf()
}