package de.fabmax.kool.demo.globe.height

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f

interface HeightMapProvider {
    fun getHeightMapAt(lat: Double, lon: Double, resolution: Double): HeightMap?
}

interface HeightMap {
    val isAvailable: Boolean

    fun contains(lat: Double, lon: Double): Boolean

    fun getHeightAt(lat: Double, lon: Double): Double

    fun getNormalAt(lat: Double, lon: Double, result: MutableVec3f): MutableVec3f
}

class NullHeightMap : HeightMapProvider, HeightMap {
    override val isAvailable = true

    override fun getHeightMapAt(lat: Double, lon: Double, resolution: Double): HeightMap = this

    override fun contains(lat: Double, lon: Double): Boolean = true

    override fun getHeightAt(lat: Double, lon: Double): Double = 0.0

    override fun getNormalAt(lat: Double, lon: Double, result: MutableVec3f): MutableVec3f = result.set(Vec3f.Z_AXIS)
}
