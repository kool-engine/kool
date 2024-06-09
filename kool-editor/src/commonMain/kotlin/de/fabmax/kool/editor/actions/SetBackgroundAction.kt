package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.components.SceneBackgroundComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.SceneBackgroundComponentData
import de.fabmax.kool.util.launchOnMainThread

class SetBackgroundAction(
    entityId: EntityId,
    val oldBackground: SceneBackgroundComponentData,
    val newBackground: SceneBackgroundComponentData
) : ComponentAction<SceneBackgroundComponent>(entityId, SceneBackgroundComponent::class) {

    override fun doAction() {
        component?.setPersistent(newBackground)
        launchOnMainThread {
            // refresh scene tree to update skybox visibility (delayed, so that it's called after bg was applied)
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }

    override fun undoAction() {
        component?.setPersistent(oldBackground)
        launchOnMainThread {
            // refresh scene tree to update skybox visibility (delayed, so that it's called after bg was applied)
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }
}