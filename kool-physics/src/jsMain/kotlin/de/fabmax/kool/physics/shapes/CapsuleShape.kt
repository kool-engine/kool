package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btCapsuleShape
import de.fabmax.kool.physics.Physics

actual class CapsuleShape actual constructor(height: Float, radius: Float) : CommonCapsuleShape(height, radius), CollisionShape {

    override val btShape: btCapsuleShape

    init {
        Physics.checkIsLoaded()

        btShape = Ammo.btCapsuleShape(radius, height)
    }
}