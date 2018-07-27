package de.fabmax.kool.modules.globe.elevation

import de.fabmax.kool.math.*
import kotlin.math.cos
import kotlin.math.sin

class ElevationMapS16(val data: ShortArray, override val meta: ElevationMapMeta) : BoundedElevationMap {

    override val west: Double
        get() = meta.west
    override val east: Double
        get() = meta.east
    override val south: Double
        get() = meta.south
    override val north: Double
        get() = meta.north

    override val isAvailable = true

    private val dx: Double
    private val dy: Double

    private val pixelScaleInv = MutableVec2d()

    init {
        val dLat = (meta.north - meta.south) / meta.height
        val dLon = (meta.east - meta.west) / meta.width
        dy = sin(dLat.toRad()) * EARTH_RADIUS
        dx = sin(dLon.toRad()) * cos((meta.north + meta.south).toRad() / 2.0) * EARTH_RADIUS

        pixelScaleInv.set(1.0 / meta.scaleX, 1.0 / meta.scaleY)
    }

    override fun getElevationAt(lat: Double, lon: Double): Double {
        return if (!contains(lat, lon)) {
            0.0
        } else {
            val x = (lon - meta.west) * pixelScaleInv.x
            val wx = 1.0 - x % 1.0
            val y = (lat - meta.south) * pixelScaleInv.y
            val wy = 1.0 - y % 1.0

            val h00 = this[x.toInt(), meta.height - 1 - y.toInt()] * meta.scaleZ
            val h01 = this[x.toInt()+1, meta.height - 1 - y.toInt()] * meta.scaleZ
            val h10 = this[x.toInt(), meta.height - 2 - y.toInt()] * meta.scaleZ
            val h11 = this[x.toInt()+1, meta.height - 2 - y.toInt()] * meta.scaleZ

            val h = (h00 * wx + h01 * (1-wx)) * wy + (h10 * wx + h11 * (1-wx)) * (1-wy)
            h
        }
    }

    override fun getNormalAt(lat: Double, lon: Double, result: MutableVec3f): MutableVec3f {
        if (!contains(lat, lon)) {
            result.set(Vec3f.Z_AXIS)

        } else {
            val x = ((lon - meta.west) * pixelScaleInv.x).toInt()
            val y = ((lat - meta.south) * pixelScaleInv.y).toInt()
            val h = this[x, y]

            result.set(Vec3f.ZERO)
            if (x > 0) {
                result.z += 1f
                result.x += ((this[x - 1, meta.height - 1 - y] - h) / dx).toFloat() * meta.scaleZ.toFloat()
            }
            if (x < meta.width - 1) {
                result.z += 1f
                result.x -= ((this[x + 1, meta.height - 1 - y] - h) / dx).toFloat() * meta.scaleZ.toFloat()
            }
            if (y > 0) {
                result.z += 1f
                result.y -= ((this[x, meta.height - 2 - y] - h) / dx).toFloat() * meta.scaleZ.toFloat()
            }
            if (y < meta.height - 1) {
                result.z += 1f
                result.y += ((this[x, meta.height - y] - h) / dx).toFloat() * meta.scaleZ.toFloat()
            }
            result.norm()
        }
        return result
    }

    operator fun get(x: Int, y: Int): Short =
            data[x.clamp(0, meta.width-1) + meta.width * y.clamp(0, meta.height - 1)]

    companion object {
        const val EARTH_RADIUS = 6_371_000.8
    }
}