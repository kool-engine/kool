package de.fabmax.kool.editor.model

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.data.SceneNodeData
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.Skybox
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.logE

class SceneModel(sceneData: SceneNodeData, val project: EditorProject) : EditorNodeModel(sceneData) {

    override val node: Scene
        get() = created ?: throw IllegalStateException("Scene was not yet created")

    private var created: Scene? = null
    override val isCreated: Boolean
        get() = created != null

    val nodesToNodeModels: MutableMap<Node, EditorNodeModel> = mutableMapOf()
    private val nodeModels: MutableMap<Long, SceneNodeModel> = mutableMapOf()

    val sceneBackground = getOrPutComponent { SceneBackgroundComponent(MdColor.GREY tone 900) }
    private val backgroundUpdater = getOrPutComponent { BackgroundUpdater() }

    init {
        project.entities += this
    }

    suspend fun create(): Scene {
        disposeCreatedScene()

        val scene = Scene(name)
        created = scene
        nodesToNodeModels[scene] = this
        initBackground()

        nodeData.childNodeIds.forEach { childId ->
            resolveNode(childId)?.let { addSceneNode(it, this) }
        }
        nodesToNodeModels.values.forEach { it.createComponents() }

        return scene
    }

    private suspend fun initBackground() {
        (sceneBackground.backgroundState.value as? SceneBackgroundData.Hdri)?.let {
            sceneBackground.loadedEnvironmentMaps = AppAssets.loadHdriEnvironment(node, it.hdriPath)
        }
    }

    fun disposeCreatedScene() {
        nodeModels.values.forEach { it.disposeCreatedNode() }
        nodesToNodeModels.clear()

        created?.dispose(KoolSystem.requireContext())
        created = null
        backgroundUpdater.skybox = null
    }

    private fun resolveNode(nodeId: Long): SceneNodeModel? {
        val nodeModel = nodeModels[nodeId]
        return if (nodeModel != null) nodeModel else {
            val nodeData = project.sceneNodeData[nodeId]
            if (nodeData != null) {
                SceneNodeModel(nodeData, this)
            } else {
                logE { "Failed to resolve node with ID $nodeId in scene $name" }
                null
            }
        }
    }

    suspend fun addSceneNode(nodeModel: SceneNodeModel, parent: EditorNodeModel) {
        if (!nodeModel.isCreated) {
            nodeModel.create()
        }

        project.entities += nodeModel
        project.addSceneNodeData(nodeModel.nodeData)
        nodeModels[nodeModel.nodeId] = nodeModel
        nodesToNodeModels[nodeModel.node] = nodeModel

        parent.addChild(nodeModel)

        nodeModel.getComponents<UpdateSceneBackgroundComponent>().forEach { it.updateBackground(sceneBackground) }
        nodeModel.nodeData.childNodeIds.mapNotNull { resolveNode(it) }.forEach { addSceneNode(it, nodeModel) }
    }

    fun removeSceneNode(nodeModel: SceneNodeModel, parent: EditorNodeModel) {
        nodeModel.nodeData.childNodeIds.mapNotNull { resolveNode(it) }.forEach { removeSceneNode(it, nodeModel) }

        project.entities -= nodeModel
        project.removeSceneNodeData(nodeModel.nodeData)
        nodeModels -= nodeModel.nodeId
        nodesToNodeModels -= nodeModel.node

        parent.removeChild(nodeModel)
    }

    override fun addChild(child: SceneNodeModel) {
        nodeData.childNodeIds += child.nodeId
        node.addNode(child.node)
    }

    override fun removeChild(child: SceneNodeModel) {
        nodeData.childNodeIds -= child.nodeId
        node.removeNode(child.node)
    }

    private inner class BackgroundUpdater : UpdateSceneBackgroundComponent {
        var skybox: Skybox.Cube? = null

        override suspend fun onCreate(nodeModel: EditorNodeModel) {
            updateBackground(sceneBackground)
        }

        override fun updateBackground(background: SceneBackgroundComponent) {
            sceneBackground.backgroundState.set(background.backgroundState.value)
            when (val bg = background.backgroundState.value) {
                is SceneBackgroundData.Hdri -> updateHdriBackground(background, bg)
                is SceneBackgroundData.SingleColor -> updateSingleColorBackground(bg)
            }
        }

        private fun updateSingleColorBackground(colorBg: SceneBackgroundData.SingleColor) {
            node.mainRenderPass.clearColor = colorBg.color.toColor()
            skybox?.isVisible = false
        }

        private fun updateHdriBackground(component: SceneBackgroundComponent, hdriBg: SceneBackgroundData.Hdri) {
            component.loadedEnvironmentMaps?.let { maps ->
                node.mainRenderPass.clearColor = null
                val skybox = this.skybox ?: Skybox.Cube()
                skybox.name = "Skybox"
                skybox.isVisible = true
                skybox.skyboxShader.setSingleSky(maps.reflectionMap)
                skybox.skyboxShader.lod = hdriBg.skyLod
                if (this.skybox == null) {
                    this.skybox = skybox
                }
                node.removeNode(skybox)
                node.addNode(skybox, 0)
            }
        }
    }
}
