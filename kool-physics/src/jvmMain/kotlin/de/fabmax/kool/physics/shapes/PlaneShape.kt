package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.Plane
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics.toVector3f
import de.fabmax.kool.physics.btStaticPlaneShape

@Suppress("CanBeParameter")
actual class PlaneShape actual constructor(planeNormal: Vec3f, actual val planeConstant: Float) : CollisionShape() {

    actual val planeNormal = Vec3f(planeNormal)

    override val shape: btStaticPlaneShape = btStaticPlaneShape(planeNormal.toVector3f(), planeConstant)

    actual constructor(plane: Plane) : this(plane.n, plane.toVec4().w)

}