package de.fabmax.kool.demo.globe

import de.fabmax.kool.platform.Math

/**
 * Slippy map tilename implementation
 * http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Mathematics
 */
data class TileName(val x: Int, val y: Int, val zoom: Int) {

    val ne: LatLon
    val sw: LatLon
    val center: LatLon

    init {
        val zp = (1 shl zoom)
        val s = Math.toDeg(Math.atan(Math.sinh(Math.PI - (y+1).toDouble() / (1 shl zoom) * 2 * Math.PI)))
        val w = (x+1).toDouble() / zp * 360 - 180
        val n = Math.toDeg(Math.atan(Math.sinh(Math.PI - y.toDouble() / (1 shl zoom) * 2 * Math.PI)))
        val e = x.toDouble() / zp * 360 - 180
        sw = LatLon(s, w)
        ne = LatLon(n, e)
        center = LatLon(sw.lat + (ne.lat - sw.lat) / 2, sw.lon + (ne.lon - sw.lon) / 2)
    }

    companion object {
        fun forLatLng(latLon: LatLon, zoom: Int): TileName = forLatLng(latLon.lat, latLon.lon, zoom)

        fun forLatLng(lat: Double, lon: Double, zoom: Int): TileName {
            val latRad = lat * Math.DEG_2_RAD
            val zp = (1 shl zoom)
            val x = Math.clamp(((lon + 180.0) / 360 * zp).toInt(), 0, zp-1)
            val y = Math.clamp(
                    ((1 - Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / Math.PI)/ 2 * zp).toInt(),
                    0, zp-1)
            return TileName(x, y, zoom)
        }
    }

    override fun toString(): String {
        return "$zoom/$x/$y"
    }
}

data class LatLon(val lat: Double, val lon: Double)
