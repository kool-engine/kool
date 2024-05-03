package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.util.sceneNodeModel

abstract class SceneNodeAction(sceneNodes: List<SceneNodeModel>): EditorAction {
    private val sceneNodeIds = sceneNodes.map { it.nodeId }

    val sceneNodes: List<SceneNodeModel> get() = sceneNodeIds.mapNotNull { it.sceneNodeModel }
    val sceneNode: SceneNodeModel? get() = sceneNodeIds.firstOrNull()?.sceneNodeModel
}
