package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class SceneNodeData(
    var name: String,
    val nodeId: Long,
    var isVisible: Boolean = true,
    var maxNumLights: Int = 4
) {
    val components: MutableList<ComponentData> = mutableListOf()
    val childNodeIds: MutableSet<Long> = mutableSetOf()
}