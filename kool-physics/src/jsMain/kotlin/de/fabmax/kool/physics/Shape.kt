package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.geometry.CollisionGeometry
import physx.PxShape

actual class Shape actual constructor(
    actual val geometry: CollisionGeometry,
    actual val material: Material,
    actual val localPose: Mat4f,
    actual val simFilterData: FilterData?,
    actual val queryFilterData: FilterData?
) {

    internal var pxShape: PxShape? = null

}