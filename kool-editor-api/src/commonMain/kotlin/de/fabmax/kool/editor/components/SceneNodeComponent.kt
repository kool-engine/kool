package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel

abstract class SceneNodeComponent : EditorModelComponent {

    private var _sceneNode: SceneNodeModel? = null
    val sceneNode: SceneNodeModel
        get() = requireNotNull(_sceneNode) { "SceneNodeComponent was not yet created" }

    val isCreated: Boolean
        get() = _sceneNode != null

    val scene: SceneModel
        get() = sceneNode.scene

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        _sceneNode = requireNotNull(nodeModel as? SceneNodeModel) {
            "SceneNodeComponent is only allowed as member of SceneNodeModels (but node is of type ${nodeModel::class})"
        }
    }

    override suspend fun initComponent(nodeModel: EditorNodeModel) { }
}