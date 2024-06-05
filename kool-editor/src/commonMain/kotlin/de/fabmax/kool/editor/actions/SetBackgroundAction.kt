package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.components.SceneBackgroundComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.util.launchOnMainThread

class SetBackgroundAction(
    entityId: EntityId,
    val oldBackground: SceneBackgroundData,
    val newBackground: SceneBackgroundData
) : ComponentAction<SceneBackgroundComponent>(entityId, SceneBackgroundComponent::class) {

    override fun doAction() {
        component?.backgroundState?.set(newBackground)
        launchOnMainThread {
            // refresh scene tree to update skybox visibility (delayed, so that it's called after bg was applied)
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }

    override fun undoAction() {
        component?.backgroundState?.set(oldBackground)
        launchOnMainThread {
            // refresh scene tree to update skybox visibility (delayed, so that it's called after bg was applied)
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }
}