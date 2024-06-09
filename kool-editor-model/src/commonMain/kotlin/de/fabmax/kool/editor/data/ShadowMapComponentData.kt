package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
data class ShadowMapComponentData(
    val shadowMap: ShadowMapTypeData,
    val clipNear: Float = 0.5f,
    val clipFar: Float = 200f
) : ComponentData

@Serializable
sealed class ShadowMapTypeData {
    @Serializable
    class Single(val mapInfo: ShadowMapInfo) : ShadowMapTypeData()

    @Serializable
    class Cascaded(val mapInfos: List<ShadowMapInfo>) : ShadowMapTypeData()
}

@Serializable
data class ShadowMapInfo(
    val mapSize: Int = 2048,
    val rangeNear: Float = 0f,
    val rangeFar: Float = 1f
)