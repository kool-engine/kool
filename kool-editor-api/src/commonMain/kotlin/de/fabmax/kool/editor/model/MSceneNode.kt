package de.fabmax.kool.editor.model

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.data.MeshComponentData
import de.fabmax.kool.editor.data.ModelComponentData
import de.fabmax.kool.editor.data.SceneNodeData
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.ColorMesh
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Model
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.MdColor

class MSceneNode(val nodeData: SceneNodeData) : MNode {

    override val nodeId: Long
        get() = nodeData.nodeId
    override val name: String
        get() = nodeData.name
    override val node: Node
        get() = created ?: throw IllegalStateException("Node was not yet created")

    private var created: Node? = null
    val isCreated: Boolean
        get() = created != null

    val modificationCount = mutableStateOf(0)

    override fun addChild(child: MSceneNode) {
        nodeData.childIds += child.nodeId
        child.nodeData.parentId = nodeId
        node.addNode(child.node)
    }

    override fun removeChild(child: MSceneNode) {
        nodeData.childIds -= child.nodeId
        node.removeNode(child.node)
    }

    suspend fun create() {
        disposeCreatedNode()

        var createdNode: Node? = null

        if (nodeData.components.any { it is ModelComponentData }) {
            createdNode = createModelNode()
        }
        if (nodeData.components.any { it is MeshComponentData }) {
            val mesh = createMeshNode()
            if (createdNode == null) {
                createdNode = mesh
            } else {
                //createdNode.addNode(mesh)
                TODO("Mesh + model in same node is not supported right now")
            }
        }
        if (createdNode == null) {
            createdNode = createPlainNode()
        }

        createdNode.name = nodeData.name
        nodeData.transform.toTransform(createdNode.transform)
        created = createdNode
    }

    private fun addMeshComponent(meshComp: MeshComponentData, node: Node?): Node {
        val mesh: Mesh = (node as? Mesh)
            ?: node?.let { it.children.first { child -> child is Mesh } as Mesh }
            ?: ColorMesh(name).apply {
                shader = KslPbrShader {
                    color { constColor(MdColor.LIGHT_GREEN.toLinear()) }
                }
            }
        meshComp.shape.generate(MeshBuilder(mesh.geometry))
        return mesh
    }

    private suspend fun createModelNode(): Model {
        val modelData = nodeData.components.first { it is ModelComponentData } as ModelComponentData
        val model = Assets.loadGltfModel(modelData.modelPath)
        model.name = name
        return model
    }

    private fun createMeshNode(): Mesh {
        return ColorMesh(name).apply {
            shader = KslPbrShader {
                color { constColor(MdColor.LIGHT_GREEN.toLinear()) }
            }
            regenerateMesh(this)
        }
    }

    private fun createPlainNode(): Node {
        return Node()
    }

    fun markModified() {
        modificationCount.set(modificationCount.value + 1)
    }

    fun disposeCreatedNode() {
        created?.dispose(KoolSystem.requireContext())
        created = null
    }

    fun regenerateMesh() {
        (node as? Mesh)?.let { regenerateMesh(it) }
    }

    private fun regenerateMesh(target: Mesh) {
        target.generate {
            nodeData.components.filterIsInstance<MeshComponentData>().forEach {
                it.shape.generate(this)
            }
        }
    }
}
