package de.fabmax.kool.physics.geometry

import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Heightmap

expect fun HeightField(heightMap: Heightmap, rowScale: Float, columnScale: Float): HeightField

abstract class HeightField : BaseReleasable() {
    abstract val heightMap: Heightmap
    abstract val rowScale: Float
    abstract val columnScale: Float
    abstract val heightScale: Float

    abstract var releaseWithGeometry: Boolean
}
