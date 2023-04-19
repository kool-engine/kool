package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.api.ClassFactory
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
    private val mutNodesToNodeModels = mutableMapOf<Node, MSceneNode<*>>()
    val nodesToNodeModels: Map<Node, MSceneNode<*>> get() = mutNodesToNodeModels

    override fun create(classFactory: ClassFactory): Scene {
        mutNodesToNodeModels.clear()

        val scene = Scene(name = nodeProperties.name).apply {
            mainRenderPass.clearColor = clearColor.toColor()

            val sceneNodes = mutableMapOf<List<String>, Pair<Node, MSceneNode<*>>>()
            groupNodes.forEach { groupModel ->
                val node = groupModel.create(classFactory)
                sceneNodes[groupModel.nodeProperties.hierarchyPath] = node to groupModel
            }
            meshes.forEach { meshModel ->
                val mesh = meshModel.create(classFactory)
                sceneNodes[meshModel.nodeProperties.hierarchyPath] = mesh to meshModel
            }

            sceneNodes.values.forEach { (node, model) -> mutNodesToNodeModels[node] = model }
            restoreHierarchy(sceneNodes)
        }
        mutNodesToNodeModels[scene] = this
        created = scene
        return scene
    }

    private fun Scene.restoreHierarchy(sceneNodes: Map<List<String>, Pair<Node, MSceneNode<*>>>) {
        sceneNodes.forEach { (hierarchyPath, node) ->
            val parent = getOrCreateNodeByPath(hierarchyPath.subList(0, hierarchyPath.lastIndex), sceneNodes)
            parent.addNode(node.first)
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
            val parent = getOrCreateNodeByPath(path.subList(0, path.lastIndex), sceneNodes)
            parent.addNode(node)
        }
        return node
    }
}
