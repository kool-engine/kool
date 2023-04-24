package de.fabmax.kool.editor.model

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.logE
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class MScene(
    override val nodeProperties: MCommonNodeProperties,
    val clearColor: MColor,

    val groupNodes: MutableMap<Long, MGroupNode> = mutableMapOf(),
    val meshes: MutableMap<Long, MMesh> = mutableMapOf(),
    //todo: val models: MutableMap<Long, MModel>
) : MSceneNode<Scene> {

    @Transient
    override var created: Scene? = null

    @Transient
    override val childNodes: MutableMap<Long, MSceneNode<*>> = mutableMapOf()

    @Transient
    val nodesToNodeModels: MutableMap<Node, MSceneNode<*>> = mutableMapOf()

    override fun create(): Scene {
        nodesToNodeModels.clear()

        val scene = Scene(name = nodeProperties.name).apply {
            mainRenderPass.clearColor = clearColor.toColor()
        }
        nodesToNodeModels[scene] = this
        created = scene

        nodeProperties.children.forEach { childId ->
            addSceneNode(childId, this)
        }
        return scene
    }

    fun getSceneNode(id: Long): MSceneNode<*>? {
        val sceneNode = groupNodes[id] ?: meshes[id]
        if (sceneNode == null) {
            logE { "Scene node not found (id: $id)" }
        }
        return sceneNode
    }

    fun addSceneNode(id: Long, parent: MSceneNode<*>) {
        val toBeCreated = getSceneNode(id) ?: return
        addSceneNode(toBeCreated, parent)
    }

    fun addSceneNode(node: MSceneNode<*>, parent: MSceneNode<*>) {
        val nodeId = node.nodeProperties.id
        when (node) {
            is MMesh -> meshes[nodeId] = node
            is MGroupNode -> groupNodes[nodeId] = node
            else -> throw IllegalArgumentException("Unknown node type: $node")
        }

        val parentNode = parent.created ?: throw IllegalStateException("Parent node must be created first")
        val createdNode = node.create()
        nodesToNodeModels[createdNode] = node
        parentNode.addNode(createdNode)

        parent.nodeProperties.children += nodeId
        parent.childNodes[nodeId] = node

        if (node.childNodes.keys.containsAll(node.nodeProperties.children)) {
            node.childNodes.values.forEach { childNode ->
                addSceneNode(childNode, node)
            }
        } else {
            node.nodeProperties.children.forEach { childId ->
                addSceneNode(childId, node)
            }
        }
    }

    fun removeSceneNode(node: MSceneNode<*>, parent: MSceneNode<*>) {
        val nodeId = node.nodeProperties.id
        when (node) {
            is MMesh -> meshes -= nodeId
            is MGroupNode -> groupNodes -= nodeId
            else -> throw IllegalArgumentException("Unknown node type: $node")
        }
        // also remove children of node
        node.childNodes.values.forEach { child ->
            removeSceneNode(child, node)
        }

        parent.nodeProperties.children -= nodeId

        node.created?.let { createdNode ->
            parent.created?.removeNode(createdNode)
            createdNode.dispose(KoolSystem.requireContext())
            node.created = null
        }
    }
}
