package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.data.SceneBackgroundComponentData
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.ecs.UpdateSceneBackgroundComponent

class SetBackgroundAction(
    val sceneModel: SceneModel,
    val oldBackground: SceneBackgroundComponentData,
    val newBackground: SceneBackgroundComponentData
) : EditorAction {
    override fun apply() = UpdateSceneBackgroundComponent.updateBackground(sceneModel, newBackground)
    override fun undo() = UpdateSceneBackgroundComponent.updateBackground(sceneModel, oldBackground)
}