package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel

interface EditorAction {
    fun doAction()
    fun undoAction()

    fun apply() {
        EditorActions.applyAction(this)
    }
}

fun sceneModel(sceneId: NodeId): SceneModel? {
    return KoolEditor.instance.projectModel.createdScenes.values.find { it.nodeId == sceneId }
}

fun sceneNodeModel(nodeId: NodeId, sceneId: NodeId): SceneNodeModel? {
    return sceneModel(sceneId)?.nodeModels?.get(nodeId)
}
