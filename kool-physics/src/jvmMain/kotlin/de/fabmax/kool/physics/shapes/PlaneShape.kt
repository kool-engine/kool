package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Plane
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.BtStaticPlaneShape
import de.fabmax.kool.physics.toBtVector3f
import de.fabmax.kool.util.BoundingBox

@Suppress("CanBeParameter")
actual class PlaneShape actual constructor(planeNormal: Vec3f, planeConstant: Float) : CommonPlaneShape(planeNormal, planeConstant), CollisionShape {

    actual constructor(plane: Plane) : this(plane.n, plane.toVec4().w)

    override val btShape: BtStaticPlaneShape = BtStaticPlaneShape(planeNormal.toBtVector3f(), planeConstant)

    private val boundsHelper = ShapeBoundsHelper(btShape)
    override fun getAabb(result: BoundingBox) = boundsHelper.getAabb(result)
    override fun getBoundingSphere(result: MutableVec4f) = boundsHelper.getBoundingSphere(result)

}