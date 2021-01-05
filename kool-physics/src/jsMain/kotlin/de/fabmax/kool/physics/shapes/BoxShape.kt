package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btBoxShape
import ammo.toBtVector3
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.util.BoundingBox

actual class BoxShape actual constructor(size: Vec3f) : CommonBoxShape(size), CollisionShape {

    override val btShape: btBoxShape

    init {
        Physics.checkIsLoaded()

        val halfExtents = size.scale(0.5f, MutableVec3f())
        btShape = Ammo.btBoxShape(halfExtents.toBtVector3())
    }

    private val boundsHelper = ShapeBoundsHelper(btShape)
    override fun getAabb(result: BoundingBox) = boundsHelper.getAabb(result)
    override fun getBoundingSphere(result: MutableVec4f) = boundsHelper.getBoundingSphere(result)

}