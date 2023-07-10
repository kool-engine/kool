package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class SceneBackgroundComponentData(var sceneBackground: SceneBackgroundData) : ComponentData

@Serializable
sealed interface SceneBackgroundData {
    @Serializable
    data class SingleColor(val color: ColorData) : SceneBackgroundData

    @Serializable
    data class Hdri(val hdriPath: String, val skyLod: Float = 1.5f) : SceneBackgroundData
}

