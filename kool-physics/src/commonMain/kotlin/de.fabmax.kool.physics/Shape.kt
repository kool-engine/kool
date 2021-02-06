package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.geometry.CollisionGeometry

class Shape(
    val geometry: CollisionGeometry,
    val material: Material,
    val localPose: Mat4f = Mat4f(),
    val simFilterData: FilterData? = null,
    val queryFilterData: FilterData? = null
)