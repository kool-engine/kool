package de.fabmax.kool.editor.model

import de.fabmax.kool.scene.Node
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
data class MGroupNode(
    override val nodeProperties: MCommonNodeProperties,
) : MSceneNode<Node> {

    @Transient
    override var created: Node? = null

    override fun create(): Node {
        val node = Node(nodeProperties.name)
        nodeProperties.transform.toTransform(node.transform)
        created = node
        return node
    }

}