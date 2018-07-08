package de.fabmax.kool.demo.globe

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f

interface HeightMap {

    fun getHeightAt(lat: Double, lon: Double, tileName: TileName): Double

    fun getNormalAt(lat: Double, lon: Double, tileName: TileName, result: MutableVec3f): MutableVec3f
}

class NullHeightMap : HeightMap {
    override fun getHeightAt(lat: Double, lon: Double, tileName: TileName): Double = 0.0

    override fun getNormalAt(lat: Double, lon: Double, tileName: TileName, result: MutableVec3f): MutableVec3f = result.set(Vec3f.Z_AXIS)
}
