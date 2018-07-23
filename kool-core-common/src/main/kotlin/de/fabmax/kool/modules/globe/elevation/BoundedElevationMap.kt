package de.fabmax.kool.modules.globe.elevation

import de.fabmax.kool.math.FUZZY_EQ_F
import de.fabmax.kool.util.Log

interface BoundedElevationMap : ElevationMap {
    val west: Double
    val east: Double
    val south: Double
    val north: Double

    val centerLat: Double get() = (north + south) / 2.0
    val centerLon: Double get() = (east + west) / 2.0

    override fun contains(lat: Double, lon: Double) = lat in (south - FUZZY_EQ_F)..(north + FUZZY_EQ_F)
            && lon in (west - FUZZY_EQ_F)..(east + FUZZY_EQ_F)

}

fun loadHeightMap(baseDir: String, meta: ElevationMapMeta): BoundedElevationMap {
    Log.d("loadHeightMap") { meta.name }
    return when (meta.format) {
        "png_s16_rg" -> loadPngS16ElevationMap(baseDir, meta)
        else -> throw NotImplementedError("Unknown format ${meta.format}")
    }
}

expect fun loadPngS16ElevationMap(basePath: String, meta: ElevationMapMeta): BoundedElevationMap
