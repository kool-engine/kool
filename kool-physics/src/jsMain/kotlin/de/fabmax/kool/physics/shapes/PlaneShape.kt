package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btStaticPlaneShape
import ammo.toBtVector3
import de.fabmax.kool.math.Plane
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics

@Suppress("CanBeParameter")
actual class PlaneShape actual constructor(planeNormal: Vec3f, actual val planeConstant: Float) : CollisionShape() {

    actual val planeNormal = Vec3f(planeNormal)

    override val shape: btStaticPlaneShape

    actual constructor(plane: Plane) : this(plane.n, plane.toVec4().w)

    init {
        Physics.checkIsLoaded()

        shape = Ammo.btStaticPlaneShape(planeNormal.toBtVector3(), planeConstant)
    }
}