package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class SceneNodeData(
    var name: String,
    val nodeId: Long,
    var isVisible: Boolean = true,
    var maxNumLights: Int = 4,
    var cameraNodeId: Long = -1L
) {
    val components: MutableList<ComponentData> = mutableListOf()
    val childNodeIds: MutableList<Long> = mutableListOf()
}