package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel

abstract class SceneNodeComponent(override val nodeModel: SceneNodeModel) : EditorModelComponent(nodeModel) {
    val sceneModel: SceneModel
        get() = nodeModel.sceneModel
}