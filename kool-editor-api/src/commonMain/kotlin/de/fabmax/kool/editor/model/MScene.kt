package de.fabmax.kool.editor.model

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.logE
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class MScene(override val nodeId: Long) : MSceneNode(), Creatable<Scene> {

    var clearColor: MColor = MColor(MdColor.GREY tone 900)

    val sceneNodes: MutableSet<MSceneNode> = mutableSetOf()

    @Transient
    val nodesToNodeModels: MutableMap<Node, MSceneNode> = mutableMapOf()
    @Transient
    private val resolvedNodes: MutableMap<Long, MSceneNode> = mutableMapOf()

    override val creatable: Creatable<out Node>
        get() = this

    @Transient
    private var created: Scene? = null

    override fun getOrNull() = created

    override suspend fun getOrCreate() = created ?: create()

    override suspend fun create(): Scene {
        disposeCreatedNode()

        val scene = Scene(name = name).apply {
            mainRenderPass.clearColor = clearColor.toColor()
        }
        created = scene
        nodesToNodeModels[scene] = this

        childIds.forEach { childId ->
            getSceneNode<MSceneNode>(childId)?.let { addSceneNode(it) }
        }
        return scene
    }

    override fun disposeCreatedNode() {
        resolvedNodes.values.filter { it !== this }.forEach { it.disposeCreatedNode() }
        nodesToNodeModels.values.filter { it !== this }.forEach { it.disposeCreatedNode() }
        nodesToNodeModels.clear()

        created?.dispose(KoolSystem.requireContext())
        created = null
    }

    inline fun <reified T: MSceneNode> getSceneNode(id: Long): T? {
        val sceneNode = resolveNodes()[id]
        return if (sceneNode is T) {
            sceneNode
        } else {
            logE { "Scene node not found or has incorrect type (id: $id, is ${sceneNode}, should be ${T::class})" }
            null
        }
    }

    fun resolveNodes(): Map<Long, MSceneNode> {
        if (resolvedNodes.isEmpty()) {
            resolvedNodes[nodeId] = this
            sceneNodes.forEach {
                resolvedNodes[it.nodeId] = it
            }
        }
        return resolvedNodes
    }

    suspend fun addSceneNode(nodeModel: MSceneNode) {
        if (nodeModel is MScene) {
            throw IllegalArgumentException("MSceneNodes cannot be nested")
        }

        val nodeId = nodeModel.nodeId
        val node = nodeModel.creatable.getOrCreate()
        sceneNodes += nodeModel
        resolvedNodes[nodeId] = nodeModel
        nodesToNodeModels[node] = nodeModel

        (getSceneNode<MSceneNode>(nodeModel.parentId) ?: this).let { parent ->
            val parentNode = parent.creatable.getOrCreate()
            parentNode.addNode(node)
            parent.childIds += nodeId
            parent.resolvedChildren[nodeId] = nodeModel
            nodeModel.parentId = parent.nodeId
        }

        nodeModel.childIds.forEach { subChildId ->
            getSceneNode<MSceneNode>(subChildId)?.let { addSceneNode(it) }
        }
    }

    fun removeSceneNode(nodeModel: MSceneNode) {
        val childNodes = nodeModel.childIds.mapNotNull { getSceneNode<MSceneNode>(it) }
        childNodes.forEach {
            removeSceneNode(it)
        }

        val nodeId = nodeModel.nodeId
        val node = nodeModel.created
        sceneNodes -= nodeModel
        resolvedNodes -= nodeId
        nodesToNodeModels.remove(node)

        getSceneNode<MSceneNode>(nodeModel.parentId)?.let { parent ->
            node?.let { parent.created?.removeNode(it) }
            parent.childIds -= nodeId
            parent.resolvedChildren -= nodeId
        }

//        node?.dispose(KoolSystem.requireContext())
//        // also remove children of node
//        nodeModel.resolvedChildren.values.forEach { subChildModel ->
//            removeSceneNode(subChildModel)
//        }
    }
}
