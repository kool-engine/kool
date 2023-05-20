package de.fabmax.kool.editor.data

import de.fabmax.kool.util.Color
import kotlinx.serialization.Serializable

@Serializable
class SceneBackgroundComponentData(var sceneBackground: SceneBackgroundData) : ComponentData

@Serializable
sealed interface SceneBackgroundData {
    @Serializable
    data class SingleColor(val color: ColorData) : SceneBackgroundData {
        constructor(color: Color) : this(ColorData(color))
    }

    @Serializable
    data class Hdri(val hdriPath: String, val skyLod: Float = 1.5f) : SceneBackgroundData
}

