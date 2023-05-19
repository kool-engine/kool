package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.data.SceneBackgroundData

interface EditorEventListener

fun interface SceneBackgroundListener : EditorEventListener {
    fun onBackgroundChanged(background: SceneBackgroundData)
}
