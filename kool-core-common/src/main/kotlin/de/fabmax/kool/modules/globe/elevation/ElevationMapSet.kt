package de.fabmax.kool.modules.globe.elevation

import de.fabmax.kool.math.isFuzzyEqual
import kotlin.math.floor

class ElevationMapSet(metas: List<ElevationMapMeta>) {

    val subMaps = mutableMapOf<Int, ElevationMapMeta>()
    private var tileDegX = 0.0
    private var tileDegY = 0.0

    init {
        metas.forEach {
            if (tileDegX == 0.0) {
                tileDegX = it.east - it.west
            } else if (!isFuzzyEqual(tileDegX, it.east - it.west)) {
                throw RuntimeException("All tiles in set must be of equal size [${it.name}: ${it.east - it.west} != $tileDegX]")
            }
            if (tileDegY == 0.0) {
                tileDegY = it.north - it.south
            } else if (!isFuzzyEqual(tileDegY, it.north - it.south)) {
                throw RuntimeException("All tiles in set must be of equal size [${it.name}: ${it.north - it.south} != $tileDegY]")
            }

            val x = lonToX((it.west + it.east) / 2.0, tileDegX)
            val y = latToY((it.south + it.north) / 2.0, tileDegY)
            subMaps[xyToKey(x, y)] = it
        }
    }

    fun getMetaAt(lat: Double, lon: Double): ElevationMapMeta? = getMetaAt(lonToX(lon, tileDegX), latToY(lat, tileDegY))?.also {
        if (!it.contains(lat, lon)) {
            println("map doesn't contain lat/lon!")
        }
    }

    fun getMetaAt(x: Int, y: Int): ElevationMapMeta? = subMaps[xyToKey(x, y)]

    companion object {
        fun lonToX(lon: Double, tileDegX: Double) = floor((lon + 180.0) / tileDegX).toInt()
        fun latToY(lat: Double, tileDegY: Double) = floor((lat + 90.0) / tileDegY).toInt()
        fun xyToKey(x: Int, y: Int) = (y shl 16) or x
        fun keyToX(key: Int) = key and 0xffff
        fun keyToY(key: Int) = key shr 16
    }
}