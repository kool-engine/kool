package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.Plane
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.BtStaticPlaneShape
import de.fabmax.kool.physics.toBtVector3f

@Suppress("CanBeParameter")
actual class PlaneShape actual constructor(planeNormal: Vec3f, planeConstant: Float) : CommonPlaneShape(planeNormal, planeConstant), CollisionShape {

    override val btShape: BtStaticPlaneShape = BtStaticPlaneShape(planeNormal.toBtVector3f(), planeConstant)

    actual constructor(plane: Plane) : this(plane.n, plane.toVec4().w)

}