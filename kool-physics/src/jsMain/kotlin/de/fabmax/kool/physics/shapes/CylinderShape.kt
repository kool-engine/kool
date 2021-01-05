package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btCylinderShape
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.util.BoundingBox

actual class CylinderShape actual constructor(height: Float, radius: Float) : CommonCylinderShape(height, radius), CollisionShape {

    override val btShape: btCylinderShape

    init {
        Physics.checkIsLoaded()

        val halfExtents = Ammo.btVector3(radius, height / 2, radius)
        btShape = Ammo.btCylinderShape(halfExtents)
    }

    private val boundsHelper = ShapeBoundsHelper(btShape)
    override fun getAabb(result: BoundingBox) = boundsHelper.getAabb(result)
    override fun getBoundingSphere(result: MutableVec4f) = boundsHelper.getBoundingSphere(result)

}