package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.components.UpdateSceneBackgroundComponent
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.util.launchOnMainThread

class SetBackgroundAction(
    val sceneModel: SceneModel,
    val oldBackground: SceneBackgroundData,
    val newBackground: SceneBackgroundData
) : EditorAction {

    override fun apply() {
        setSceneBackground(newBackground)
    }

    override fun undo() {
        setSceneBackground(oldBackground)
    }

    private fun setSceneBackground(bgData: SceneBackgroundData) {
        if (bgData is SceneBackgroundData.Hdri) {
            launchOnMainThread {
                sceneModel.sceneBackground.loadedEnvironmentMaps = AppAssets.loadHdriEnvironment(sceneModel.node, bgData.hdriPath)
                sceneModel.sceneBackground.backgroundState.set(bgData)
                UpdateSceneBackgroundComponent.updateSceneBackground(sceneModel)
                // refresh scene tree to update skybox visibility
                KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
            }
        } else {
            sceneModel.sceneBackground.loadedEnvironmentMaps = null
            sceneModel.sceneBackground.backgroundState.set(bgData)
            UpdateSceneBackgroundComponent.updateSceneBackground(sceneModel)
            // refresh scene tree to update skybox visibility
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }
}