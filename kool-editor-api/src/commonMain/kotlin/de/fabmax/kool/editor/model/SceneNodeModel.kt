package de.fabmax.kool.editor.model

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.data.SceneNodeData
import de.fabmax.kool.editor.data.TransformComponentData
import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.editor.model.ecs.*
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.launchOnMainThread

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

    private val backgroundUpdater = getOrPutComponent { BackgroundUpdater() }

    override fun addChild(child: SceneNodeModel) {
        nodeData.childNodeIds += child.nodeId
        node.addNode(child.node)
    }

    override fun removeChild(child: SceneNodeModel) {
        nodeData.childNodeIds -= child.nodeId
        node.removeNode(child.node)
    }

    suspend fun create() {
        val ibl = scene.sceneBackground.loadedEnvironmentMaps
        backgroundUpdater.isIblShaded = ibl != null

        disposeCreatedNode()
        var createdNode: Node? = null
        for (meshComponent in getComponents<MeshComponent>()) {
            val mesh = meshComponent.createMesh(ibl)
            meshComponentMeshes[meshComponent] = mesh
            if (createdNode == null) {
                createdNode = mesh
            } else {
                createdNode.addNode(mesh)
            }
        }

        for (modelComponent in getComponents<ModelComponent>()) {
            val model = modelComponent.createModel(ibl)
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

    private suspend fun ModelComponent.createModel(ibl: EnvironmentMaps?): Model {
        val modelCfg = GltfFile.ModelGenerateConfig(
            materialConfig = GltfFile.ModelMaterialConfig(environmentMaps = ibl)
        )
        val model = AppAssets.loadModel(componentData).makeModel(modelCfg)
        model.name = name
        return model
    }

    private fun MeshComponent.createMesh(ibl: EnvironmentMaps?): Mesh {
        return ColorMesh(name).apply {
            shader = defaultPbrShader(ibl)
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

    private fun defaultPbrShader(ibl: EnvironmentMaps?): KslPbrShader {
        return KslPbrShader {
            color { vertexColor() }
            ibl?.let {
                enableImageBasedLighting(ibl)
            }
        }
    }

    private fun replaceCreatedNode(newNode: Node) {
        created?.let {
            it.parent?.let {  parent ->
                val ndIdx = parent.children.indexOf(it)
                parent.removeNode(it)
                parent.addNode(newNode, ndIdx)
            }
            scene.nodesToNodeModels -= it
            it.dispose(KoolSystem.requireContext())
        }
        transform.transformState.value.toTransform(newNode.transform)
        created = newNode
        scene.nodesToNodeModels[newNode] = this
    }

    private inner class BackgroundUpdater : UpdateSceneBackgroundComponent {
        var skybox: Skybox.Cube? = null
        var isIblShaded = false

        override fun updateBackground(background: SceneBackgroundComponent) {
            when (val bg = background.backgroundState.value) {
                is SceneBackgroundData.Hdri -> background.loadedEnvironmentMaps?.let { updateHdriBg(it) }
                is SceneBackgroundData.SingleColor -> updateSingleColorBg(bg.color.toColor().toLinear())
            }
        }

        private fun updateSingleColorBg(bgColor: Color) {
            meshComponentMeshes.values.forEach { mesh ->
                val isLit = mesh.shader is KslLitShader
                if (isLit) {
                    if (isIblShaded) {
                        mesh.shader = defaultPbrShader(null)
                    }
                    (mesh.shader as KslLitShader).ambientFactor = bgColor
                }
            }

            if (isIblShaded) {
                // recreate models without ibl lighting
                recreateModels(null)
            } else {
                modelComponentModels.values.forEach { model ->
                    model.meshes.values.forEach { mesh ->
                        (mesh.shader as? KslLitShader)?.ambientFactor = bgColor
                    }
                }
            }
            isIblShaded = false
        }

        private fun updateHdriBg(maps: EnvironmentMaps) {
            meshComponentMeshes.values.forEach { mesh ->
                val isLit = mesh.shader is KslLitShader
                if (isLit) {
                    if (!isIblShaded) {
                        mesh.shader = defaultPbrShader(maps)
                    } else {
                        (mesh.shader as KslLitShader).ambientMap = maps.irradianceMap
                        (mesh.shader as? KslPbrShader)?.reflectionMap = maps.reflectionMap
                    }
                }
            }
            if (!isIblShaded) {
                // recreate models with ibl lighting
                recreateModels(maps)
            } else {
                modelComponentModels.values.forEach { model ->
                    model.meshes.values.forEach { mesh ->
                        (mesh.shader as? KslLitShader)?.ambientMap = maps.irradianceMap
                        (mesh.shader as? KslPbrShader)?.reflectionMap = maps.reflectionMap
                    }
                }
            }
            isIblShaded = true
        }

        private fun recreateModels(ibl: EnvironmentMaps?) {
            launchOnMainThread {
                modelComponentModels.forEach { (modelComponent, oldModel) ->
                    val newModel = modelComponent.createModel(ibl)
                    if (node == oldModel) {
                        replaceCreatedNode(newModel)
                    } else {
                        val idx = node.children.indexOf(oldModel)
                        node.removeNode(oldModel)
                        node.addNode(newModel, idx)
                    }
                }
            }
        }
    }
}