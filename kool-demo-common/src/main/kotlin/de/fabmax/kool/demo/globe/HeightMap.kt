package de.fabmax.kool.demo.globe

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f

interface HeightMap {

    fun getHeightAt(lat: Double, lon: Double, resolutionMas: Double): Double

    fun getNormalAt(lat: Double, lon: Double, resolutionMas: Double, result: MutableVec3f): MutableVec3f
}

class NullHeightMap : HeightMap {
    override fun getHeightAt(lat: Double, lon: Double, resolutionMas: Double): Double = 0.0

    override fun getNormalAt(lat: Double, lon: Double, resolutionMas: Double, result: MutableVec3f): MutableVec3f = result.set(Vec3f.Z_AXIS)
}
