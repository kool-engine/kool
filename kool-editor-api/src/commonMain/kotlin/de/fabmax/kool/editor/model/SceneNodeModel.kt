package de.fabmax.kool.editor.model

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.components.TransformComponent
import de.fabmax.kool.editor.data.SceneNodeData
import de.fabmax.kool.editor.data.TransformComponentData
import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.scene.Node

class SceneNodeModel(nodeData: SceneNodeData, val parent: EditorNodeModel, val sceneModel: SceneModel) : EditorNodeModel(nodeData) {

    override var drawNode: Node = Node(nodeData.name)
        private set

    val transform = getOrPutComponent { TransformComponent(this, TransformComponentData(TransformData.IDENTITY)) }

    init {
        nameState.onChange { drawNode.name = it }
    }

    override fun addChild(child: SceneNodeModel) {
        nodeData.childNodeIds += child.nodeId
        drawNode.addNode(child.drawNode)
    }

    override fun removeChild(child: SceneNodeModel) {
        nodeData.childNodeIds -= child.nodeId
        drawNode.removeNode(child.drawNode)
    }

    fun disposeAndClearCreatedNode() {
        drawNode.dispose(KoolSystem.requireContext())
        drawNode.parent?.removeNode(drawNode)
    }

    fun setDrawNode(newDrawNode: Node) {
        val oldDrawNode = drawNode
        var ndIdx = -1
        oldDrawNode.parent?.let { parent ->
            ndIdx = parent.children.indexOf(oldDrawNode)
            parent.removeNode(oldDrawNode)
        }

        // todo: newDrawNode.children += oldDrawNode.children
        newDrawNode.onUpdate += oldDrawNode.onUpdate
        oldDrawNode.onUpdate.clear()
        oldDrawNode.dispose(KoolSystem.requireContext())

        transform.transformState.value.toTransform(newDrawNode.transform)
        newDrawNode.name = nodeData.name
        drawNode = newDrawNode

        val wasInSceneModel = sceneModel.nodesToNodeModels.remove(oldDrawNode) != null
        if (wasInSceneModel) {
            sceneModel.nodesToNodeModels[newDrawNode] = this
            parent.drawNode.addNode(newDrawNode, ndIdx)
        }
    }
}