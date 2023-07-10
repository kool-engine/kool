package de.fabmax.kool.editor.model

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.components.EditorModelComponent
import de.fabmax.kool.editor.components.SceneBackgroundComponent
import de.fabmax.kool.editor.components.UpdateSceneBackgroundComponent
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.data.SceneNodeData
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.Skybox
import de.fabmax.kool.util.*

class SceneModel(sceneData: SceneNodeData, val project: EditorProject) : EditorNodeModel(sceneData) {

    override val drawNode: Scene
        get() = created ?: throw IllegalStateException("Scene was not yet created")

    private var created: Scene? = null
    override val isCreated: Boolean
        get() = created != null

    val nodesToNodeModels: MutableMap<Node, EditorNodeModel> = mutableMapOf()
    private val nodeModels: MutableMap<Long, SceneNodeModel> = mutableMapOf()

    val sceneBackground = getOrPutComponent { SceneBackgroundComponent(MdColor.GREY toneLin 900) }
    private val backgroundUpdater = getOrPutComponent { BackgroundUpdater() }

    val shaderData = SceneShaderData()

    init {
        project.entities += this
        nameState.onChange { created?.name = it }
    }

    suspend fun createScene() {
        disposeCreatedScene()

        val scene = Scene(name)
        // clear default lighting
        scene.lighting.clear()

        created = scene
        nodesToNodeModels[scene] = this

        createComponents()
    }

    override suspend fun createComponents() {
        super.createComponents()

        nodeData.childNodeIds.forEach { childId ->
            resolveNode(childId, this)?.let {
                if (!it.isCreated) {
                    it.createComponents()
                }
                addSceneNode(it)
            }
        }
    }

    private fun disposeCreatedScene() {
        nodeModels.values.forEach { it.disposeAndClearCreatedNode() }
        nodesToNodeModels.clear()

        created?.dispose(KoolSystem.requireContext())
        created = null
        backgroundUpdater.skybox = null
    }

    private fun resolveNode(nodeId: Long, parent: EditorNodeModel): SceneNodeModel? {
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

    fun addSceneNode(nodeModel: SceneNodeModel) {
        require(nodeModel.isCreated) { "SceneNodeModel needs to be created before being added to SceneModel" }

        project.entities += nodeModel
        project.addSceneNodeData(nodeModel.nodeData)
        nodeModels[nodeModel.nodeId] = nodeModel
        nodesToNodeModels[nodeModel.drawNode] = nodeModel
        nodeModel.parent.addChild(nodeModel)

        nodeModel.getComponents<UpdateSceneBackgroundComponent>().forEach { it.updateBackground(sceneBackground) }
        nodeModel.nodeData.childNodeIds
            .mapNotNull { resolveNode(it, nodeModel) }
            .forEach { addSceneNode(it) }
        nodeModel.onNodeAdded()
    }

    fun removeSceneNode(nodeModel: SceneNodeModel) {
        nodeModel.nodeData.childNodeIds.mapNotNull { nodeModels[nodeId] }.forEach { removeSceneNode(it) }

        project.entities -= nodeModel
        project.removeSceneNodeData(nodeModel.nodeData)
        nodeModels -= nodeModel.nodeId
        nodesToNodeModels -= nodeModel.drawNode

        nodeModel.parent.removeChild(nodeModel)

        launchDelayed(1) {
            // dispose but don't clear draw node (so that we can add it again on undo)
            nodeModel.drawNode.dispose(KoolSystem.requireContext())
            nodeModel.onNodeRemoved()
        }
    }

    override fun addChild(child: SceneNodeModel) {
        nodeData.childNodeIds += child.nodeId
        drawNode.addNode(child.drawNode)
    }

    override fun removeChild(child: SceneNodeModel) {
        nodeData.childNodeIds -= child.nodeId
        drawNode.removeNode(child.drawNode)
    }

    private inner class BackgroundUpdater :
        EditorModelComponent(),
        UpdateSceneBackgroundComponent
    {
        var skybox: Skybox.Cube? = null

        override suspend fun createComponent(nodeModel: EditorNodeModel) {
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
        var environmentMaps: EnvironmentMaps? = null
        var ambientColorLinear: Color = Color.BLACK

        val shadowMaps = mutableListOf<ShadowMap>()

        var ssaoMap: Texture2d? = null
    }
}
