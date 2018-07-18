package de.fabmax.kool.demo.globe.height

import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class HeightMapMeta(
        @SerialId(1) val path: String,
        @SerialId(2) val format: String,
        @SerialId(3) val attr: String,

        @SerialId(4) val width: Int,
        @SerialId(5) val height: Int,

        @SerialId(6) val north: Double,
        @SerialId(7) val south: Double,
        @SerialId(8) val east: Double,
        @SerialId(9) val west: Double,

        @SerialId(10) val scaleX: Double,
        @SerialId(11) val scaleY: Double,
        @SerialId(12) val scaleZ: Double
) {

    /**
     * Lateral height map resolution in arc-seconds
     */
    @Transient
    val resolutionLat: Double
        get() = (north - south) * 3600.0 / height

    /**
     * Longitudinal height map resolution in arc-seconds
     */
    @Transient
    val resolutionLon: Double
        get() = (east - west) * 3600.0 / height

    fun contains(lat: Double, lon: Double) =
            lat in (south - scaleY)..(north + scaleY) && lon in (west - scaleX)..(east + scaleX)

}

@Serializable
data class HeightMapMetaHierarchy(
        @SerialId(1) val maps: Map<Double, List<HeightMapMeta>>
)
