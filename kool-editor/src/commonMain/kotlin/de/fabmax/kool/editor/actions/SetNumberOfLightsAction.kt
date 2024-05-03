package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.util.sceneModel

class SetNumberOfLightsAction(val sceneId: NodeId, val newMaxNumLights: Int) : EditorAction {

    private val sceneModel: SceneModel? get() = sceneId.sceneModel
    private val oldMaxNumLights = sceneModel?.maxNumLightsState?.value

    override fun doAction() {
        sceneModel?.maxNumLightsState?.set(newMaxNumLights)
    }

    override fun undoAction() {
        oldMaxNumLights?.let { sceneModel?.maxNumLightsState?.set(it) }
    }
}