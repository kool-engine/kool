package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.geometry.CollisionGeometry

expect class ShapeHolder

class Shape(
    val geometry: CollisionGeometry,
    val material: Material = Physics.defaultMaterial,
    localPose: Mat4f = Mat4f.IDENTITY,
    val simFilterData: FilterData? = null,
    val queryFilterData: FilterData? = null
) {
    val localPose: Mat4f = Mat4f(localPose)
    internal var holder: ShapeHolder? = null
}
