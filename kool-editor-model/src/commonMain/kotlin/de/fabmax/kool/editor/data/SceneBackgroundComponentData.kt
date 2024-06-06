package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
data class SceneBackgroundComponentData(val sceneBackground: SceneBackgroundData) : ComponentData

@Serializable
sealed interface SceneBackgroundData {
    @Serializable
    data class SingleColor(val color: ColorData) : SceneBackgroundData

    @Serializable
    data class Hdri(val hdriPath: String, val skyLod: Float = 1.5f) : SceneBackgroundData
}
