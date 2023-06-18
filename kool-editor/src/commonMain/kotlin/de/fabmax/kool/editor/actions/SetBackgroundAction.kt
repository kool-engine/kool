package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.util.launchOnMainThread

class SetBackgroundAction(
    val sceneModel: SceneModel,
    val oldBackground: SceneBackgroundData,
    val newBackground: SceneBackgroundData
) : EditorAction {

    override fun apply() {
        sceneModel.sceneBackground.backgroundState.set(newBackground)
        launchOnMainThread {
            // refresh scene tree to update skybox visibility (delayed, so that it's called after bg was applied)
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }

    override fun undo() {
        sceneModel.sceneBackground.backgroundState.set(oldBackground)
        launchOnMainThread {
            // refresh scene tree to update skybox visibility (delayed, so that it's called after bg was applied)
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }
}