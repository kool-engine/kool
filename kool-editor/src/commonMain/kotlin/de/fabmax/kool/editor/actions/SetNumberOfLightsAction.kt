package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.api.sceneComponent
import de.fabmax.kool.editor.components.SceneComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.util.gameEntity

class SetNumberOfLightsAction(val sceneId: EntityId, val newMaxNumLights: Int) : EditorAction {

    private val sceneModel: SceneComponent? get() = sceneId.gameEntity?.sceneComponent
    private val oldMaxNumLights = sceneModel?.maxNumLightsState?.value

    override fun doAction() {
        sceneModel?.maxNumLightsState?.set(newMaxNumLights)
    }

    override fun undoAction() {
        oldMaxNumLights?.let { sceneModel?.maxNumLightsState?.set(it) }
    }
}