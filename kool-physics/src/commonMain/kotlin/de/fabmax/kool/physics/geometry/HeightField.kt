package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.Releasable
import de.fabmax.kool.util.HeightMap

expect fun HeightField(heightMap: HeightMap, rowScale: Float, columnScale: Float): HeightField

interface HeightField : Releasable {
    val heightMap: HeightMap
    val rowScale: Float
    val columnScale: Float
    val heightScale: Float

    var releaseWithGeometry: Boolean

    override fun release()
}
