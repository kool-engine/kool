package de.fabmax.kool.editor.model

import de.fabmax.kool.scene.Node
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class MSceneNode {

    var name: String = "not-set"
    var transform: MTransform = MTransform.IDENTITY

    abstract val nodeId: Long
    var parentId: Long = -1L
    val childIds: MutableSet<Long> = mutableSetOf()

    @Transient
    val resolvedChildren: MutableMap<Long, MSceneNode> = mutableMapOf()

    abstract val creatable: Creatable<out Node>
}

val MSceneNode.created: Node? get() = creatable.getOrNull()
