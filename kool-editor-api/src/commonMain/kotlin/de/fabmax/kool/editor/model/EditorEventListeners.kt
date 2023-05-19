package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.data.SceneBackgroundData

interface EditorEventListener

fun interface SceneBackgroundListener : EditorEventListener {
    fun onBackgroundChanged(background: SceneBackgroundData)

    companion object {
        fun invoke(background: SceneBackgroundData, scene: MScene) {
            scene.sceneEditorEventListeners.filterIsInstance<SceneBackgroundListener>().forEach {
                it.onBackgroundChanged(background)
            }
        }
    }
}
