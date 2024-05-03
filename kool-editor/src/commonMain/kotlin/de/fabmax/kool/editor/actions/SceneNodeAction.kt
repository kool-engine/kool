package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel

abstract class SceneNodeAction(sceneNodes: List<SceneNodeModel>): EditorAction {
    val sceneId = sceneNodes.firstOrNull()?.sceneModel?.nodeId
    val sceneNodeIds = sceneNodes.map { it.nodeId }

    val sceneModel: SceneModel? get() = sceneId?.let { sceneModel(it) }
    val nodeModels: List<SceneNodeModel>
        get() = sceneModel?.let { scene -> sceneNodeIds.mapNotNull { scene.nodeModels[it] } } ?: emptyList()
    val nodeModel: SceneNodeModel? get() = nodeModels.firstOrNull()

    protected fun resolveNodeModel(parentId: Long): NodeModel? {
        return if (sceneModel?.nodeId == parentId) {
            sceneModel
        } else {
            sceneModel?.nodeModels?.get(parentId)
        }
    }

}

fun sceneModel(sceneId: Long): SceneModel? {
    return KoolEditor.instance.projectModel.getCreatedScenes().find { it.nodeId == sceneId }
}

fun sceneNodeModel(nodeId: Long, sceneId: Long): SceneNodeModel? {
    return sceneModel(sceneId)?.nodeModels?.get(nodeId)
}
