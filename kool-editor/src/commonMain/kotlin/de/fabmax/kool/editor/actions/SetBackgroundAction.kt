package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.components.SceneBackgroundComponent
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.util.launchOnMainThread

class SetBackgroundAction(
    val backgroundComponent: SceneBackgroundComponent,
    val oldBackground: SceneBackgroundData,
    val newBackground: SceneBackgroundData
) : EditorAction {

    override fun doAction() {
        backgroundComponent.backgroundState.set(newBackground)
        launchOnMainThread {
            // refresh scene tree to update skybox visibility (delayed, so that it's called after bg was applied)
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }

    override fun undoAction() {
        backgroundComponent.backgroundState.set(oldBackground)
        launchOnMainThread {
            // refresh scene tree to update skybox visibility (delayed, so that it's called after bg was applied)
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }
}