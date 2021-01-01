package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btCapsuleShape
import de.fabmax.kool.physics.Physics

@Suppress("CanBeParameter")
actual class CapsuleShape actual constructor(actual val height: Float, actual val radius: Float) : CollisionShape() {

    override val shape: btCapsuleShape

    init {
        Physics.checkIsLoaded()

        shape = Ammo.btCapsuleShape(radius, height)
    }
}