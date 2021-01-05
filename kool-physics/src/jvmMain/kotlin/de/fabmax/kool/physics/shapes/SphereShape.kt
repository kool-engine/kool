package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.physics.BtSphereShape
import de.fabmax.kool.util.BoundingBox

@Suppress("CanBeParameter")
actual class SphereShape actual constructor(radius: Float) : CommonSphereShape(radius), CollisionShape {

    override val btShape: BtSphereShape = BtSphereShape(radius)

    private val boundsHelper = ShapeBoundsHelper(btShape)
    override fun getAabb(result: BoundingBox) = boundsHelper.getAabb(result)
    override fun getBoundingSphere(result: MutableVec4f) = boundsHelper.getBoundingSphere(result)

}