package de.fabmax.kool.demo.globe.height

import de.fabmax.kool.math.FUZZY_EQ_F
import de.fabmax.kool.util.Log

interface BoundedHeightMap : HeightMap {
    val west: Double
    val east: Double
    val south: Double
    val north: Double

    val centerLat: Double get() = (north + south) / 2.0
    val centerLon: Double get() = (east + west) / 2.0

    override fun contains(lat: Double, lon: Double) = lat in (south - FUZZY_EQ_F)..(north + FUZZY_EQ_F)
            && lon in (west - FUZZY_EQ_F)..(east + FUZZY_EQ_F)

}

fun loadHeightMap(baseDir: String, meta: HeightMapMeta): BoundedHeightMap {
    Log.d("loadHeightMap") { meta.path }
    return when (meta.format) {
        "png_s16_rg" -> loadPngS16HeightMap(baseDir, meta)
        else -> throw NotImplementedError("Unknown format ${meta.format}")
    }
}

expect fun loadPngS16HeightMap(basePath: String, meta: HeightMapMeta): BoundedHeightMap
