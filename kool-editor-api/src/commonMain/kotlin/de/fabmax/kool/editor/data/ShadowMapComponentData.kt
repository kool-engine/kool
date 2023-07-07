package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class ShadowMapComponentData(var shadowMap: ShadowMapData) : ComponentData

@Serializable
sealed class ShadowMapData {
    @Serializable
    class Single(val mapInfo: ShadowMapInfo) : ShadowMapData()

    @Serializable
    class Cascaded(val mapInfos: List<ShadowMapInfo>) : ShadowMapData()
}

@Serializable
data class ShadowMapInfo(
    val mapWidth: Int,
    val mapHeight: Int,
    val rangeNear: Float,
    val rangeFar: Float
)