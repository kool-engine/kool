package de.fabmax.kool.modules.globe.elevation

import de.fabmax.kool.AssetManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoId

@Serializable
data class ElevationMapMeta(
        @ProtoId(1) val name: String,
        @ProtoId(2) val format: String,
        @ProtoId(3) val attr: String,

        @ProtoId(4) val width: Int,
        @ProtoId(5) val height: Int,

        @ProtoId(6) val north: Double,
        @ProtoId(7) val south: Double,
        @ProtoId(8) val east: Double,
        @ProtoId(9) val west: Double,

        @ProtoId(10) val scaleX: Double,
        @ProtoId(11) val scaleY: Double,
        @ProtoId(12) val scaleZ: Double
) {

    /**
     * Lateral height map resolution in arc-seconds
     */
    val resolutionLat: Double
        get() = (north - south) * 3600.0 / height

    /**
     * Longitudinal height map resolution in arc-seconds
     */
    val resolutionLon: Double
        get() = (east - west) * 3600.0 / height

    fun contains(lat: Double, lon: Double) =
            lat in (south - scaleY)..(north + scaleY) && lon in (west - scaleX)..(east + scaleX)

}

@Serializable
data class ElevationMapMetaHierarchy(
        @ProtoId(1) val maps: Map<Double, List<ElevationMapMeta>>
)

fun loadElevationMap(baseDir: String, meta: ElevationMapMeta, assetMgr: AssetManager): BoundedElevationMap {
    return when (meta.format) {
        "png_s16_rg" -> loadPngS16ElevationMap(baseDir, meta, assetMgr)
        else -> throw NotImplementedError("Unknown format ${meta.format}")
    }
}

expect fun loadPngS16ElevationMap(basePath: String, meta: ElevationMapMeta, assetMgr: AssetManager): BoundedElevationMap