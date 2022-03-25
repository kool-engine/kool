package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.Releasable
import de.fabmax.kool.util.HeightMap

expect class HeightField(heightMap: HeightMap, rowScale: Float, columnScale: Float) : Releasable {
    val heightMap: HeightMap
    val rowScale: Float
    val columnScale: Float

    var releaseWithGeometry: Boolean
}
