package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btSphereShape
import de.fabmax.kool.physics.Physics

@Suppress("CanBeParameter")
actual class SphereShape actual constructor(actual val radius: Float) : CollisionShape() {

    override val shape: btSphereShape

    init {
        Physics.checkIsLoaded()

        shape = Ammo.btSphereShape(radius)
    }
}