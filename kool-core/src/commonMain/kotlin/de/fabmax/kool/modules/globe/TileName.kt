package de.fabmax.kool.modules.globe

import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.toDeg
import de.fabmax.kool.math.toRad
import kotlin.math.*

/**
 * Slippy map tilename implementation
 * http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Mathematics
 */
open class TileName(val x: Int, val y: Int, val zoom: Int) {

    val latN: Double
    val latS: Double
    val lonE: Double
    val lonW: Double

    val latCenter: Double
    val lonCenter: Double

    val fusedKey: Long = fuesdKey(x, y, zoom)

    init {
        val zp = (1 shl zoom)
        latS = atan(sinh(PI - (y+1).toDouble() / (1 shl zoom) * 2 * PI)).toDeg()
        lonW = (x+1).toDouble() / zp * 360.0 - 180.0
        latN = atan(sinh(PI - y.toDouble() / (1 shl zoom) * 2 * PI)).toDeg()
        lonE = x.toDouble() / zp * 360.0 - 180.0

        latCenter = latS + (latN - latS) * 0.5
        lonCenter = lonE + (lonW - lonE) * 0.5
    }

    fun isSubTileOf(parent: TileName): Boolean {
        return if (parent.zoom > zoom) {
            false
        } else {
            val projX = x shr (zoom - parent.zoom)
            val projY = y shr (zoom - parent.zoom)
            projX == parent.x && projY == parent.y
        }
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

    companion object {
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

        fun fromFusedKey(fusedKey: Long): TileName {
            val zoom = (fusedKey shr 58).toInt()
            val x = ((fusedKey shr 29) and 0x1fffffff).toInt()
            val y = (fusedKey and 0x1fffffff).toInt()
            return TileName(x, y, zoom)
        }
    }
}
