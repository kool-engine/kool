package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.BtStaticPlaneShape
import de.fabmax.kool.physics.toBtVector3f
import de.fabmax.kool.util.BoundingBox

@Suppress("CanBeParameter")
actual class PlaneShape : CommonPlaneShape(), CollisionShape {

    override val btShape: BtStaticPlaneShape = BtStaticPlaneShape(Vec3f.X_AXIS.toBtVector3f(), 0f)

    private val boundsHelper = ShapeBoundsHelper(btShape)
    override fun getAabb(result: BoundingBox) = boundsHelper.getAabb(result)
    override fun getBoundingSphere(result: MutableVec4f) = boundsHelper.getBoundingSphere(result)

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f) = getBtInertia(mass, result)
}