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

    val groups: MutableSet<MGroup> = mutableSetOf(),
    val meshes: MutableSet<MMesh> = mutableSetOf(),
    //todo: val models: MutableMap<Long, MModel>
) : MSceneNode<Scene> {

    @Transient
    override var created: Scene? = null

    @Transient
    override val childNodes: MutableMap<Long, MSceneNode<*>> = mutableMapOf()
    @Transient
    val nodesToNodeModels: MutableMap<Node, MSceneNode<*>> = mutableMapOf()

    @Transient
    private val groupsByIds: MutableMap<Long, MGroup> = groups.associateBy { it.nodeProperties.id }.toMutableMap()
    @Transient
    private val meshesByIds: MutableMap<Long, MMesh> = meshes.associateBy { it.nodeProperties.id }.toMutableMap()

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
        val sceneNode = groupsByIds[id] ?: meshesByIds[id]
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
            is MMesh -> {
                meshes += node
                meshesByIds[nodeId] = node
            }
            is MGroup -> {
                groups += node
                groupsByIds[nodeId] = node
            }
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
            is MMesh -> {
                meshes -= node
                meshesByIds -= nodeId
            }
            is MGroup -> {
                groups -= node
                groupsByIds -= nodeId
            }
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
