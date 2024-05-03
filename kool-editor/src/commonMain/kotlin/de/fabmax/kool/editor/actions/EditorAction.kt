package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel

interface EditorAction {
    fun doAction()
    fun undoAction()

    fun apply() {
        EditorActions.applyAction(this)
    }
}

fun sceneModel(sceneId: Long): SceneModel? {
    return KoolEditor.instance.projectModel.getCreatedScenes().find { it.nodeId == sceneId }
}

fun sceneNodeModel(nodeId: Long, sceneId: Long): SceneNodeModel? {
    return sceneModel(sceneId)?.nodeModels?.get(nodeId)
}
