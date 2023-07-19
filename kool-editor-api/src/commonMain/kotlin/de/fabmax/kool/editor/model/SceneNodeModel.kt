package de.fabmax.kool.editor.model

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.components.TransformComponent
import de.fabmax.kool.editor.data.SceneNodeData
import de.fabmax.kool.editor.data.TransformComponentData
import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Node

class SceneNodeModel(nodeData: SceneNodeData, var parent: EditorNodeModel, val sceneModel: SceneModel) : EditorNodeModel(nodeData) {

    override var drawNode: Node = Node(nodeData.name)
        private set

    val transform = getOrPutComponent { TransformComponent(this, TransformComponentData(TransformData.IDENTITY)) }

    private val nodeUpdateCb: (RenderPass.UpdateEvent) -> Unit = { ev -> onNodeUpdate.forEach { cb -> cb(ev) } }

    init {
        nameState.onChange { drawNode.name = it }
        drawNode.onUpdate += nodeUpdateCb
        transform.applyTransformTo(drawNode)
    }

    override fun addChild(child: SceneNodeModel) {
        nodeData.childNodeIds += child.nodeId
        child.parent = this
        drawNode.addNode(child.drawNode)
    }

    override fun removeChild(child: SceneNodeModel) {
        nodeData.childNodeIds -= child.nodeId
        drawNode.removeNode(child.drawNode)
    }

    fun setDrawNode(newDrawNode: Node) {
        val oldDrawNode = drawNode
        var ndIdx = -1
        oldDrawNode.parent?.let { parent ->
            ndIdx = parent.children.indexOf(oldDrawNode)
            parent.removeNode(oldDrawNode)
        }

        val childNodes = nodeData.childNodeIds.mapNotNull { sceneModel.nodeModels[it] }
        childNodes.forEach {
            oldDrawNode.removeNode(it.drawNode)
            newDrawNode.addNode(it.drawNode)
        }
        oldDrawNode.onUpdate -= nodeUpdateCb
        oldDrawNode.dispose(KoolSystem.requireContext())

        transform.transformState.value.toTransform(newDrawNode.transform)
        newDrawNode.name = nodeData.name
        drawNode = newDrawNode
        drawNode.onUpdate += nodeUpdateCb

        val wasInSceneModel = sceneModel.nodesToNodeModels.remove(oldDrawNode) != null
        if (wasInSceneModel) {
            sceneModel.nodesToNodeModels[newDrawNode] = this
            parent.drawNode.addNode(newDrawNode, ndIdx)
        }
    }
}