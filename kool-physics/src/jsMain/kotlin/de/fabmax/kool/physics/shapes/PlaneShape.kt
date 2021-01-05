package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btStaticPlaneShape
import ammo.toBtVector3
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Plane
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.util.BoundingBox

actual class PlaneShape actual constructor(planeNormal: Vec3f, planeConstant: Float) : CommonPlaneShape(planeNormal, planeConstant), CollisionShape {

    override val btShape: btStaticPlaneShape

    init {
        Physics.checkIsLoaded()

        btShape = Ammo.btStaticPlaneShape(planeNormal.toBtVector3(), planeConstant)
    }

    private val boundsHelper = ShapeBoundsHelper(btShape)
    override fun getAabb(result: BoundingBox) = boundsHelper.getAabb(result)
    override fun getBoundingSphere(result: MutableVec4f) = boundsHelper.getBoundingSphere(result)

    actual constructor(plane: Plane) : this(plane.n, plane.toVec4().w)

}