package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btCylinderShape
import de.fabmax.kool.physics.Physics

actual class CylinderShape actual constructor(height: Float, radius: Float) : CommonCylinderShape(height, radius), CollisionShape {

    override val btShape: btCylinderShape

    init {
        Physics.checkIsLoaded()

        val halfExtents = Ammo.btVector3(radius, height / 2, radius)
        btShape = Ammo.btCylinderShape(halfExtents)
    }
}