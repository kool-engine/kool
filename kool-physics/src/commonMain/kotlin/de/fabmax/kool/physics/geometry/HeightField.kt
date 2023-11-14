package de.fabmax.kool.physics.geometry

import de.fabmax.kool.util.HeightMap
import de.fabmax.kool.util.Releasable

expect fun HeightField(heightMap: HeightMap, rowScale: Float, columnScale: Float): HeightField

interface HeightField : Releasable {
    val heightMap: HeightMap
    val rowScale: Float
    val columnScale: Float
    val heightScale: Float

    var releaseWithGeometry: Boolean
}
