package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btCylinderShape
import de.fabmax.kool.physics.Physics

@Suppress("CanBeParameter")
actual class CylinderShape actual constructor(actual val radius: Float, actual val height: Float) : CollisionShape() {

    override val shape: btCylinderShape

    init {
        Physics.checkIsLoaded()

        val halfExtents = Ammo.btVector3(radius, height / 2, radius)
        shape = Ammo.btCylinderShape(halfExtents)
    }
}