package de.fabmax.kool.physics.geometry

import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.HeightMap

expect fun HeightField(heightMap: HeightMap, rowScale: Float, columnScale: Float): HeightField

abstract class HeightField : BaseReleasable() {
    abstract val heightMap: HeightMap
    abstract val rowScale: Float
    abstract val columnScale: Float
    abstract val heightScale: Float

    abstract var releaseWithGeometry: Boolean
}
