package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.EditorModelComponent
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel

abstract class ComponentAction<T: EditorModelComponent>(component: T) : EditorAction {
    private val sceneId = when (val model = component.nodeModel) {
        is SceneModel -> model.nodeId
        is SceneNodeModel -> model.sceneModel.nodeId
    }
    private val nodeModelId = component.nodeModel.nodeId

    val nodeModel: NodeModel? get() {
        val scene = sceneModel(sceneId)
        return if (nodeModelId == sceneId) scene else scene?.nodeModels?.get(nodeModelId)
    }
}