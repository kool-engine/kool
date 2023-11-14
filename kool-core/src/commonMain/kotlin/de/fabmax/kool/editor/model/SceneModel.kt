package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.components.*
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.data.SceneNodeData
import de.fabmax.kool.editor.data.ScenePropertiesComponentData
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.Skybox
import de.fabmax.kool.util.*

class SceneModel(sceneData: SceneNodeData, val project: EditorProject) : NodeModel(sceneData) {

    val sceneProperties = getOrPutComponent { ScenePropertiesComponent(this, ScenePropertiesComponentData()) }

    val shaderData = SceneShaderData()

    val maxNumLightsState = mutableStateOf(sceneProperties.componentData.maxNumLights).onChange {
        shaderData.maxNumberOfLights = it
        if (AppState.isEditMode) {
            sceneProperties.componentData.maxNumLights = it
        }
        drawNode.lighting.maxNumberOfLights = it
        project.getComponentsInScene<UpdateMaxNumLightsComponent>(this).forEach { comp ->
            comp.updateMaxNumLightsComponent(it)
        }
    }

    val cameraState = mutableStateOf<CameraComponent?>(null).onChange {
        if (AppState.isEditMode) {
            sceneProperties.componentData.cameraNodeId = it?.nodeModel?.nodeId ?: -1L
        } else {
            // only set scene cam if not in edit mode. In edit mode, editor camera is used instead
            it?.camera?.let { cam -> drawNode.camera = cam }
        }
        project.getComponentsInScene<UpdateSceneCameraComponent>(this).forEach { comp ->
            comp.updateSceneCameraComponent(it?.camera)
        }
    }

    override var drawNode: Scene = Scene(name)

    val nodesToNodeModels: MutableMap<Node, NodeModel> = mutableMapOf()
    val nodeModels: MutableMap<Long, SceneNodeModel> = mutableMapOf()
    val sceneNodes: List<SceneNodeModel> get() = nodesToNodeModels.values.filterIsInstance<SceneNodeModel>()

    val sceneBackground = getOrPutComponent { SceneBackgroundComponent(this, MdColor.GREY toneLin 900) }
    private val backgroundUpdater = getOrPutComponent { BackgroundUpdater() }

    init {
        shaderData.maxNumberOfLights = maxNumLightsState.value
        project.entities += this
    }

    suspend fun createScene() {
        disposeCreatedScene()

        drawNode = Scene(name).apply {
            onUpdate { ev ->
                onNodeUpdate.forEach { it(ev) }
            }
        }
        nodesToNodeModels[drawNode] = this

        maxNumLightsState.set(sceneProperties.componentData.maxNumLights)
        drawNode.lighting.apply {
            clear()
            maxNumberOfLights = maxNumLightsState.value
        }

        createComponents()
    }

    override suspend fun createComponents() {
        super.createComponents()

        nodeData.childNodeIds.forEach { childId ->
            resolveNode(childId, this)?.let {
                addSceneNode(it)
            }
        }

        val cam = nodeModels[sceneProperties.componentData.cameraNodeId]?.getComponent<CameraComponent>()
        if (cam != null) {
            cameraState.set(cam)
            drawNode.camera = cam.camera
        } else {
            logW { "Scene $name has no camera attached" }
        }
    }

    private fun disposeCreatedScene() {
        nodeModels.values.forEach { it.destroyComponents() }
        nodesToNodeModels.clear()

        drawNode.release()
        backgroundUpdater.skybox = null
    }

    private fun resolveNode(nodeId: Long, parent: NodeModel): SceneNodeModel? {
        val nodeModel = nodeModels[nodeId]
        return if (nodeModel != null) nodeModel else {
            val nodeData = project.sceneNodeData[nodeId]
            if (nodeData != null) {
                SceneNodeModel(nodeData, parent, this)
            } else {
                logE { "Failed to resolve node with ID $nodeId in scene $name" }
                null
            }
        }
    }

    suspend fun addSceneNode(nodeModel: SceneNodeModel) {
        if (!nodeModel.isCreated) {
            nodeModel.createComponents()
        } else {
            logW { "Adding a scene node which is already created" }
        }

        project.entities += nodeModel
        project.addSceneNodeData(nodeModel.nodeData)
        nodeModels[nodeModel.nodeId] = nodeModel
        nodesToNodeModels[nodeModel.drawNode] = nodeModel
        nodeModel.parent.addChild(nodeModel)

        nodeModel.getComponents<UpdateSceneBackgroundComponent>().forEach { it.updateBackground(sceneBackground) }
        nodeModel.nodeData.childNodeIds
            .mapNotNull { resolveNode(it, nodeModel) }
            .forEach { addSceneNode(it) }
    }

    fun removeSceneNode(nodeModel: SceneNodeModel) {
        nodeModel.nodeData.childNodeIds.mapNotNull { nodeModels[nodeId] }.forEach { removeSceneNode(it) }

        project.entities -= nodeModel
        project.removeSceneNodeData(nodeModel.nodeData)
        nodeModels -= nodeModel.nodeId
        nodesToNodeModels -= nodeModel.drawNode

        nodeModel.parent.removeChild(nodeModel)

        launchDelayed(1) {
            nodeModel.destroyComponents()
        }
    }

    inline fun <reified T: Any> getComponentsInScene(): List<T> {
        return project.getComponentsInScene(this)
    }

    private inner class BackgroundUpdater :
        EditorModelComponent(this@SceneModel),
        UpdateSceneBackgroundComponent
    {
        var skybox: Skybox.Cube? = null

        override suspend fun createComponent() {
            super.createComponent()
            updateBackground(sceneBackground)
        }

        override fun updateSingleColorBg(bgColorLinear: Color) {
            drawNode.mainRenderPass.clearColor = bgColorLinear.toSrgb()
            skybox?.isVisible = false
        }

        override fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps) {
            drawNode.mainRenderPass.clearColor = null
            val skybox = this.skybox ?: Skybox.Cube()
            skybox.name = "Skybox"
            skybox.isVisible = true
            skybox.skyboxShader.setSingleSky(ibl.reflectionMap)
            skybox.skyboxShader.lod = hdriBg.skyLod
            if (this.skybox == null) {
                this.skybox = skybox
            }
            drawNode.removeNode(skybox)
            drawNode.addNode(skybox, 0)
        }
    }

    class SceneShaderData {
        var maxNumberOfLights: Int = 4

        var environmentMaps: EnvironmentMaps? = null
        var ambientColorLinear: Color = Color.BLACK

        val shadowMaps = mutableListOf<ShadowMap>()

        var ssaoMap: Texture2d? = null
    }
}

interface UpdateMaxNumLightsComponent {
    fun updateMaxNumLightsComponent(newMaxNumLights: Int)
}

interface UpdateSceneCameraComponent {
    fun updateSceneCameraComponent(newCamera: Camera?)
}
