package de.fabmax.kool.demo.globe

import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.toDeg
import de.fabmax.kool.math.toRad
import kotlin.math.*

/**
 * Slippy map tilename implementation
 * http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Mathematics
 */
open class TileName(val x: Int, val y: Int, val zoom: Int) {

    val ne: LatLon
    val sw: LatLon
    val center: LatLon

    val fusedKey: Long = fuesdKey(x, y, zoom)

    init {
        val zp = (1 shl zoom)
        val s = atan(sinh(PI - (y+1).toDouble() / (1 shl zoom) * 2 * PI)).toDeg()
        val w = (x+1).toDouble() / zp * 360 - 180
        val n = atan(sinh(PI - y.toDouble() / (1 shl zoom) * 2 * PI)).toDeg()
        val e = x.toDouble() / zp * 360 - 180
        sw = LatLon(s, w)
        ne = LatLon(n, e)
        center = LatLon(sw.lat + (ne.lat - sw.lat) / 2, sw.lon + (ne.lon - sw.lon) / 2)
    }

    companion object {
        fun forLatLon(latLon: LatLon, zoom: Int): TileName = forLatLon(latLon.lat, latLon.lon, zoom)

        fun forLatLon(lat: Double, lon: Double, zoom: Int): TileName {
            val latRad = lat.toRad()
            val zp = (1 shl zoom)
            val x = ((lon + 180.0) / 360 * zp).toInt().clamp(0, zp-1)
            val y = ((1 - ln(tan(latRad) + 1 / cos(latRad)) / PI)/ 2 * zp).toInt()
                    .clamp(0, zp-1)
            return TileName(x, y, zoom)
        }

        fun fuesdKey(tx: Int, ty: Int, tz: Int): Long = (tz.toLong() shl 58) or
                ((tx and 0x1fffffff).toLong().shl(29)) or
                (ty and 0x1fffffff).toLong()
    }

    override fun toString(): String {
        return "$zoom/$x/$y"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TileName) return false

        if (fusedKey != other.fusedKey) return false

        return true
    }

    override fun hashCode(): Int {
        return fusedKey.hashCode()
    }
}

data class LatLon(val lat: Double, val lon: Double)
