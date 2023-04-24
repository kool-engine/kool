package de.fabmax.kool.editor.model

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.logW
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class MScene(
    override val nodeProperties: MCommonNodeProperties,
    val clearColor: MColor,

    val groupNodes: MutableList<MGroupNode> = mutableListOf(),
    val meshes: MutableList<MMesh> = mutableListOf(),
    //todo: val models: List<MModel>
) : MSceneNode<Scene> {

    @Transient
    override var created: Scene? = null

    @Transient
    private val createdSceneNodes = mutableMapOf<List<String>, Pair<Node, MSceneNode<*>>>()

    @Transient
    private val mutNodesToNodeModels = mutableMapOf<Node, MSceneNode<*>>()
    val nodesToNodeModels: Map<Node, MSceneNode<*>> get() = mutNodesToNodeModels

    override fun create(): Scene {
        mutNodesToNodeModels.clear()
        createdSceneNodes.clear()

        val scene = Scene(name = nodeProperties.name).apply {
            mainRenderPass.clearColor = clearColor.toColor()

            groupNodes.forEach { groupModel ->
                val node = groupModel.create()
                createdSceneNodes[groupModel.nodeProperties.hierarchyPath] = node to groupModel
            }
            meshes.forEach { meshModel ->
                val mesh = meshModel.create()
                createdSceneNodes[meshModel.nodeProperties.hierarchyPath] = mesh to meshModel
            }

            createdSceneNodes.values.forEach { (node, model) -> mutNodesToNodeModels[node] = model }
            restoreHierarchy(createdSceneNodes)
        }
        mutNodesToNodeModels[scene] = this
        created = scene
        return scene
    }

    fun removeNode(modelNode: MSceneNode<*>) {
        when (modelNode) {
            is MMesh -> meshes -= modelNode
            is MGroupNode -> groupNodes -= modelNode
            else -> throw IllegalArgumentException("Unknown node type: $modelNode")
        }

        val createdScene = created ?: return
        val node = modelNode.created ?: return
        val parent = createdScene.getOrCreateNodeByPath(modelNode.parentPath, createdSceneNodes)
        parent.removeNode(node)

        createdSceneNodes.remove(modelNode.nodeProperties.hierarchyPath)
        mutNodesToNodeModels.remove(node)

        node.dispose(KoolSystem.requireContext())
    }

    fun addNode(modelNode: MSceneNode<*>) {
        when (modelNode) {
            is MMesh -> meshes += modelNode
            is MGroupNode -> groupNodes += modelNode
            else -> throw IllegalArgumentException("Unknown node type: $modelNode")
        }

        val createdScene = created ?: return

        val node = modelNode.created ?: modelNode.create()
        val parent = createdScene.getOrCreateNodeByPath(modelNode.parentPath, createdSceneNodes)
        parent.addNode(node)

        createdSceneNodes[modelNode.nodeProperties.hierarchyPath] = node to modelNode
        mutNodesToNodeModels[node] = modelNode
    }

    private fun Scene.restoreHierarchy(sceneNodes: Map<List<String>, Pair<Node, MSceneNode<*>>>) {
        sceneNodes.values.forEach { (node, modelNode) ->
            val parent = getOrCreateNodeByPath(modelNode.parentPath, sceneNodes)
            parent.addNode(node)
        }
    }

    private fun Scene.getOrCreateNodeByPath(
        path: List<String>,
        sceneNodes: Map<List<String>, Pair<Node, MSceneNode<*>>>
    ): Node {
        if (path.size == 1) {
            if (path[0] != name) {
                throw IllegalStateException("Path name does not match scene name (path[0]: ${path[0]}, scene name: $name)")
            }
            return this
        }
        var node = sceneNodes[path]?.first
        if (node == null) {
            logW { "Scene node at path not found: $path, inserting empty node" }
            node = Node(path.last())
            val parent = getOrCreateNodeByPath(path.parentPath, sceneNodes)
            parent.addNode(node)
        }
        return node
    }

    private val MSceneNode<*>.parentPath: List<String>
        get() = nodeProperties.hierarchyPath.parentPath

    private val List<String>.parentPath: List<String>
        get() = subList(0, lastIndex)
}
