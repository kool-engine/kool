package de.fabmax.kool.editor.model

import de.fabmax.kool.scene.Node
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class MGroup(override val nodeId: Long) : MSceneNode(), Creatable<Node> {

    override val creatable: Creatable<out Node>
        get() = this

    @Transient
    private var created: Node? = null

    override fun getOrNull() = created

    override suspend fun getOrCreate() = created ?: create()

    private fun create(): Node {
        val node = Node(name)
        transform.toTransform(node.transform)
        created = node
        return node
    }
}