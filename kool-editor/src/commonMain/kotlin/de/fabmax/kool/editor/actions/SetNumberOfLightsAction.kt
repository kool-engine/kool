package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.model.SceneModel

class SetNumberOfLightsAction(val sceneModel: SceneModel, val newMaxNumLights: Int) : EditorAction {

    private val oldMaxNumLights = sceneModel.maxNumLightsState.value

    override fun doAction() {
        sceneModel.maxNumLightsState.set(newMaxNumLights)
    }

    override fun undoAction() {
        sceneModel.maxNumLightsState.set(oldMaxNumLights)
    }
}