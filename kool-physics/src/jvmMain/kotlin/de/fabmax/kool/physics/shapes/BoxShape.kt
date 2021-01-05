package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.BtBoxShape
import de.fabmax.kool.physics.toBtVector3f
import de.fabmax.kool.util.BoundingBox

actual class BoxShape actual constructor(size: Vec3f) : CommonBoxShape(size), CollisionShape {

    override val btShape: BtBoxShape = BtBoxShape(size.scale(0.5f, MutableVec3f()).toBtVector3f())

    private val boundsHelper = ShapeBoundsHelper(btShape)
    override fun getAabb(result: BoundingBox) = boundsHelper.getAabb(result)
    override fun getBoundingSphere(result: MutableVec4f) = boundsHelper.getBoundingSphere(result)

}