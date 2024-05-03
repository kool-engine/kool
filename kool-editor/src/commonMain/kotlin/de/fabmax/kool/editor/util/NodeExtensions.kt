package de.fabmax.kool.editor.util

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.util.logE

val NodeId.isSceneId: Boolean get() = this in KoolEditor.instance.projectModel.createdScenes.keys

val NodeId.nodeModel: NodeModel? get() {
    val nodeModel = if (isSceneId) {
        KoolEditor.instance.projectModel.createdScenes[this]
    } else {
        KoolEditor.instance.projectModel.createdScenes.values.find { this in it.nodeModels }?.nodeModels?.get(this)
    }
    if (nodeModel == null) {
        logE { "node model not found: $this" }
    }
    return nodeModel
}

val NodeId.sceneModel: SceneModel? get() {
    return nodeModel as? SceneModel?
}

val NodeId.sceneNodeModel: SceneNodeModel? get() {
    return nodeModel as? SceneNodeModel?
}

val NodeModel.sceneModel: SceneModel get() = when (this) {
    is SceneModel -> this
    is SceneNodeModel -> sceneModel
}