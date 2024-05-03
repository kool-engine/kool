package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.EditorModelComponent
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneNodeModel

class RemoveComponentAction(
    nodeModel: NodeModel,
    val removeComponent: EditorModelComponent
) : EditorAction {

    private val nodeId = nodeModel.nodeId
    private val sceneId = if (nodeModel is SceneNodeModel) nodeModel.sceneModel.nodeId else nodeModel.nodeId
    private val nodeModel: NodeModel? get() {
        val scene = sceneModel(sceneId)
        return if (nodeId == sceneId) scene else scene?.nodeModels?.get(nodeId)
    }

    // fixme: component is not recreated on undo / redo, therefore redo can fail

    override fun doAction() {
        nodeModel?.removeComponent(removeComponent)
    }

    override fun undoAction() {
        nodeModel?.addComponent(removeComponent)
    }
}