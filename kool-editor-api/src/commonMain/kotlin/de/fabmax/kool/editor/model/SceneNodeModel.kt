package de.fabmax.kool.editor.model

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.data.SceneNodeData
import de.fabmax.kool.editor.data.TransformComponentData
import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.editor.model.ecs.*
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.scene.ColorMesh
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Model
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class SceneNodeModel(val nodeData: SceneNodeData, val scene: SceneModel) : EditorModelEntity(nodeData.components), EditorNodeModel {

    override val nodeId: Long
        get() = nodeData.nodeId
    override val name: String
        get() = nodeData.name
    override val node: Node
        get() = created ?: throw IllegalStateException("Node was not yet created")

    private var created: Node? = null
    val isCreated: Boolean
        get() = created != null

    val transform = getOrPutComponent { TransformComponent(TransformComponentData(TransformData.IDENTITY)) }

    private val meshComponentMeshes = mutableMapOf<MeshComponent, Mesh>()
    private val modelComponentModels = mutableMapOf<ModelComponent, Model>()

    private val backgroundUpdater = getOrPutComponent {
        UpdateSceneBackgroundComponent {
            when (val bg = it.sceneBackground) {
                is SceneBackgroundData.Hdri -> TODO()
                is SceneBackgroundData.SingleColor -> node.setBackgroundColor(bg.color.toColor().toLinear())
            }
        }
    }

    private fun Node.setBackgroundColor(bgColor: Color) {
        if (this is Mesh && (this === node || this !in scene.nodesToNodeModels)) {
            (this.shader as? KslLitShader)?.let {
                it.ambientFactor = bgColor
            }
        }
        children.forEach { it.setBackgroundColor(bgColor) }
    }

    override fun addChild(child: SceneNodeModel) {
        nodeData.childNodeIds += child.nodeId
        node.addNode(child.node)
    }

    override fun removeChild(child: SceneNodeModel) {
        nodeData.childNodeIds -= child.nodeId
        node.removeNode(child.node)
    }

    suspend fun create() {
        disposeCreatedNode()
        var createdNode: Node? = null
        for (meshComponent in getComponents<MeshComponent>()) {
            val mesh = meshComponent.createMesh()
            meshComponentMeshes[meshComponent] = mesh
            if (createdNode == null) {
                createdNode = mesh
            } else {
                createdNode.addNode(mesh)
            }
        }

        for (modelComponent in getComponents<ModelComponent>()) {
            val model = modelComponent.createModel()
            modelComponentModels[modelComponent] = model
            if (createdNode == null) {
                createdNode = model
            } else {
                createdNode.addNode(model)
            }
        }

        if (createdNode == null) {
            createdNode = Node()
        }

        createdNode.name = nodeData.name
        transform.transformState.value.toTransform(createdNode.transform)
        created = createdNode
    }

    private suspend fun ModelComponent.createModel(): Model {
        val model = Assets.loadGltfModel(componentData.modelPath)
        model.name = name
        return model
    }

    private fun MeshComponent.createMesh(): Mesh {
        return ColorMesh(name).apply {
            shader = KslPbrShader {
                color { constColor(MdColor.LIGHT_GREEN.toLinear()) }
            }
            generateGeometry(this)
        }
    }

    private fun MeshComponent.generateGeometry(target: Mesh) {
        target.generate {
            shapesState.forEach {
                withTransform {
                    it.pose.toMat4f(transform)
                    color = it.vertexColor.toColor()
                    it.generate(this)
                }
            }
        }
    }

    fun disposeCreatedNode() {
        created?.dispose(KoolSystem.requireContext())
        created = null
    }

    fun regenerateGeometry(meshComponent: MeshComponent) {
        val mesh = meshComponentMeshes[meshComponent]
        if (mesh != null) {
            meshComponent.generateGeometry(mesh)
        }
    }
}
