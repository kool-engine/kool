package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class SceneNodeData(val nodeId: Long, var parentId: Long = -1L, var name: String = "<not-set>") {
    var transform: TransformData = TransformData.IDENTITY
    val components: MutableList<ComponentData> = mutableListOf()

    val childIds: MutableSet<Long> = mutableSetOf()
}