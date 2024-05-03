package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
class SceneNodeData(
    var name: String,
    var nodeId: NodeId,
    var isVisible: Boolean = true
) {
    val components: MutableList<ComponentData> = mutableListOf()
    val childNodeIds: MutableList<NodeId> = mutableListOf()
}

@Serializable
@JvmInline
value class NodeId(val id: Long)