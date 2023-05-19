package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.model.MScene
import de.fabmax.kool.editor.model.SceneBackgroundListener

class SetBackgroundAction(
    val scene: MScene,
    val oldBackground: SceneBackgroundData
) : EditorAction {

    private val newBackground: SceneBackgroundData = scene.sceneData.background

    override fun apply() {
        scene.sceneData.background = newBackground
        SceneBackgroundListener.invoke(newBackground, scene)
    }

    override fun undo() {
        scene.sceneData.background = oldBackground
        SceneBackgroundListener.invoke(oldBackground, scene)
    }
}