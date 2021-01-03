package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btSphereShape
import de.fabmax.kool.physics.Physics

actual class SphereShape actual constructor(radius: Float) : CommonSphereShape(radius), CollisionShape {

    override val btShape: btSphereShape

    init {
        Physics.checkIsLoaded()

        btShape = Ammo.btSphereShape(radius)
    }
}