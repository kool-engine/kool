package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel

abstract class SceneNodeComponent : EditorModelComponent() {

    override val nodeModel: SceneNodeModel
        get() = super.nodeModel as SceneNodeModel

    val sceneModel: SceneModel
        get() = nodeModel.scene

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        require(nodeModel is SceneNodeModel) {
            "SceneNodeComponent is only allowed as member of SceneNodeModels (but nodeModel is of type ${nodeModel::class})"
        }
        super.createComponent(nodeModel)
    }
}