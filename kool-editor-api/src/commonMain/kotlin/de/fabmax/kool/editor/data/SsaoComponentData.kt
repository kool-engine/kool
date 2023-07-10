package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class SsaoComponentData(var settings: SsaoSettings = SsaoSettings()) : ComponentData

@Serializable
data class SsaoSettings(
    val samples: Int = 16,
    val mapSize: Float = 0.7f,
    val radius: Float = 0.05f,
    val isRelativeRadius: Boolean = true,
    val strength: Float = 1.25f,
    val power: Float = 4f
)
