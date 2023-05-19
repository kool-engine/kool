package de.fabmax.kool.editor.model

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.api.EditorEventListener
import de.fabmax.kool.editor.data.SceneData
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.logE

class MScene(var sceneData: SceneData, val project: MProject) : MNode {

    override val nodeId: Long
        get() = sceneData.sceneId
    override val name: String
        get() = sceneData.name
    override val node: Scene
        get() = created ?: throw IllegalStateException("Scene was not yet created")

    private var created: Scene? = null

    val backgroundMutableState = mutableStateOf(sceneData.background).onChange { sceneData.background = it }

    val sceneNodes: MutableSet<MSceneNode> = mutableSetOf()

    val sceneEditorEventListeners = mutableSetOf<EditorEventListener>()

    val nodesToNodeModels: MutableMap<Node, MNode> = mutableMapOf()
    private val nodeModels: MutableMap<Long, MSceneNode> = mutableMapOf()

    suspend fun create(): Scene {
        disposeCreatedScene()

        val scene = Scene(name = sceneData.name)
        created = scene
        nodesToNodeModels[scene] = this

        val sceneNodeData = sceneData.sceneNodes.associateBy { it.nodeId }.toMutableMap()
        nodeModels.keys.retainAll(sceneNodeData.keys)
        sceneNodeData.keys.removeAll(nodeModels.keys)
        sceneNodeData.values.forEach { nodeModels[it.nodeId] = MSceneNode(it) }

        sceneData.rootNodeIds.forEach { childId ->
            resolveNode(childId)?.let { addSceneNode(it) }
        }
        sceneData.background.applyBackground(scene)
        return scene
    }

    fun disposeCreatedScene() {
        sceneEditorEventListeners.clear()
        nodeModels.values.forEach { it.disposeCreatedNode() }
        nodesToNodeModels.clear()

        created?.dispose(KoolSystem.requireContext())
        created = null
    }

    private fun resolveNode(nodeId: Long): MSceneNode? {
        val nodeModel = nodeModels[nodeId]
        if (nodeModel == null) {
            logE { "Failed to resolve node with ID $nodeId" }
        }
        return nodeModel
    }

    suspend fun addSceneNode(nodeModel: MSceneNode) {
        if (!nodeModel.isCreated) {
            nodeModel.create()
        }

        sceneData.sceneNodes += nodeModel.nodeData
        sceneNodes += nodeModel
        nodeModels[nodeModel.nodeId] = nodeModel
        nodesToNodeModels[nodeModel.node] = nodeModel

        val parent = nodeModels[nodeModel.nodeData.parentId] ?: this
        parent.addChild(nodeModel)

        nodeModel.nodeData.childIds.mapNotNull { resolveNode(it) }.forEach { addSceneNode(it) }
    }

    fun removeSceneNode(nodeModel: MSceneNode) {
        nodeModel.nodeData.childIds.mapNotNull { resolveNode(it) }.forEach { removeSceneNode(it) }

        sceneData.sceneNodes -= nodeModel.nodeData
        sceneNodes -= nodeModel
        nodeModels -= nodeModel.nodeId
        nodesToNodeModels -= nodeModel.node

        val parent = nodeModels[nodeModel.nodeData.parentId] ?: this
        parent.removeChild(nodeModel)

//        val nodeId = nodeModel.nodeId
//        val node = nodeModel.created
//        sceneNodes -= nodeModel
//        resolvedNodes -= nodeId
//        nodesToNodeModels.remove(node)

//        getSceneNode<MSceneNode>(nodeModel.parentId)?.let { parent ->
//            node?.let { parent.created?.removeNode(it) }
//            parent.childIds -= nodeId
//            parent.resolvedChildren -= nodeId
//        }

//        node?.dispose(KoolSystem.requireContext())
//        // also remove children of node
//        nodeModel.resolvedChildren.values.forEach { subChildModel ->
//            removeSceneNode(subChildModel)
//        }
    }

    override fun addChild(child: MSceneNode) {
        sceneData.rootNodeIds += child.nodeId
        child.nodeData.parentId = nodeId
        node.addNode(child.node)
    }

    override fun removeChild(child: MSceneNode) {
        sceneData.rootNodeIds -= child.nodeId
        node.removeNode(child.node)
    }
}
