package de.fabmax.kool.modules.globe.elevation

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f

interface ElevationMapProvider {
    fun getElevationMapAt(lat: Double, lon: Double, resolution: Double): ElevationMap?
}

interface ElevationMap {
    val isAvailable: Boolean
    val meta: ElevationMapMeta?

    fun contains(lat: Double, lon: Double): Boolean

    fun getElevationAt(lat: Double, lon: Double): Double

    fun getNormalAt(lat: Double, lon: Double, result: MutableVec3f): MutableVec3f
}

class NullElevationMap : ElevationMapProvider, ElevationMap {
    override val isAvailable = true
    override val meta = null

    override fun getElevationMapAt(lat: Double, lon: Double, resolution: Double): ElevationMap = this

    override fun contains(lat: Double, lon: Double): Boolean = true

    override fun getElevationAt(lat: Double, lon: Double): Double = 0.0

    override fun getNormalAt(lat: Double, lon: Double, result: MutableVec3f): MutableVec3f = result.set(Vec3f.Z_AXIS)
}
