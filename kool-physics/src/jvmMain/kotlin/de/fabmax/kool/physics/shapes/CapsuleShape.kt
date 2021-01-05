package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.physics.BtCapsuleShape
import de.fabmax.kool.util.BoundingBox

actual class CapsuleShape actual constructor(height: Float, radius: Float) : CommonCapsuleShape(height, radius), CollisionShape {

    override val btShape: BtCapsuleShape = BtCapsuleShape(radius, height)

    private val boundsHelper = ShapeBoundsHelper(btShape)
    override fun getAabb(result: BoundingBox) = boundsHelper.getAabb(result)
    override fun getBoundingSphere(result: MutableVec4f) = boundsHelper.getBoundingSphere(result)

}