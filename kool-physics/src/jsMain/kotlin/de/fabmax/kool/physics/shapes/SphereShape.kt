package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btSphereShape
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.util.BoundingBox

actual class SphereShape actual constructor(radius: Float) : CommonSphereShape(radius), CollisionShape {

    override val btShape: btSphereShape

    init {
        Physics.checkIsLoaded()

        btShape = Ammo.btSphereShape(radius)
    }

    private val boundsHelper = ShapeBoundsHelper(btShape)
    override fun getAabb(result: BoundingBox) = boundsHelper.getAabb(result)
    override fun getBoundingSphere(result: MutableVec4f) = boundsHelper.getBoundingSphere(result)

}