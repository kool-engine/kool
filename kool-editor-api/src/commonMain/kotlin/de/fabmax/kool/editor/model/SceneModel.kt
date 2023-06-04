package de.fabmax.kool.editor.model

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.components.SceneBackgroundComponent
import de.fabmax.kool.editor.components.UpdateSceneBackgroundComponent
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.data.SceneNodeData
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.Skybox
import de.fabmax.kool.util.Color
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
        nameState.onChange { created?.name = it }
    }

    suspend fun createScene() {
        disposeCreatedScene()

        val scene = Scene(name)
        created = scene
        nodesToNodeModels[scene] = this

        createComponents()
        initComponents()
    }

    override suspend fun createComponents() {
        super.createComponents()

        nodeData.childNodeIds.forEach { childId ->
            resolveNode(childId)?.let {
                addSceneNode(it, this)
            }
        }
    }

    override suspend fun initComponents() {
        super.initComponents()

        nodesToNodeModels.values.filter { it !== this }.forEach {
            it.initComponents()
        }
    }

    private fun disposeCreatedScene() {
        nodeModels.values.forEach { it.disposeCreatedNode() }
        nodesToNodeModels.clear()

        created?.dispose(KoolSystem.requireContext())
        created = null
        backgroundUpdater.skybox = null
    }

    private suspend fun resolveNode(nodeId: Long): SceneNodeModel? {
        val nodeModel = nodeModels[nodeId]
        return if (nodeModel != null) nodeModel else {
            val nodeData = project.sceneNodeData[nodeId]
            if (nodeData != null) {
                SceneNodeModel(nodeData, this).also { it.createComponents() }
            } else {
                logE { "Failed to resolve node with ID $nodeId in scene $name" }
                null
            }
        }
    }

    suspend fun addSceneNode(nodeModel: SceneNodeModel, parent: EditorNodeModel) {
        project.entities += nodeModel
        project.addSceneNodeData(nodeModel.nodeData)
        nodeModels[nodeModel.nodeId] = nodeModel
        nodesToNodeModels[nodeModel.node] = nodeModel
        parent.addChild(nodeModel)

        if (nodeModel.isCreated) {
            nodeModel.getComponents<UpdateSceneBackgroundComponent>().forEach { it.updateBackground(sceneBackground) }
        }
        nodeModel.nodeData.childNodeIds.mapNotNull { resolveNode(it) }.forEach { addSceneNode(it, nodeModel) }
    }

    fun removeSceneNode(nodeModel: SceneNodeModel, parent: EditorNodeModel) {
        nodeModel.nodeData.childNodeIds.mapNotNull { nodeModels[nodeId] }.forEach { removeSceneNode(it, nodeModel) }

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

        override suspend fun createComponent(nodeModel: EditorNodeModel) { }

        override suspend fun initComponent(nodeModel: EditorNodeModel) {
            updateBackground(sceneBackground)
        }

        override fun updateSingleColorBg(bgColorSrgb: Color) {
            node.mainRenderPass.clearColor = bgColorSrgb
            skybox?.isVisible = false
        }

        override fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps) {
            node.mainRenderPass.clearColor = null
            val skybox = this.skybox ?: Skybox.Cube()
            skybox.name = "Skybox"
            skybox.isVisible = true
            skybox.skyboxShader.setSingleSky(ibl.reflectionMap)
            skybox.skyboxShader.lod = hdriBg.skyLod
            if (this.skybox == null) {
                this.skybox = skybox
            }
            node.removeNode(skybox)
            node.addNode(skybox, 0)
        }
    }
}
