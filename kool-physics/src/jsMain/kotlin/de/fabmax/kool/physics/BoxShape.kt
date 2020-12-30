package de.fabmax.kool.physics

import ammo.Ammo
import ammo.btCollisionShape
import ammo.toBtVector3
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f

actual class BoxShape actual constructor(size: Vec3f) : CollisionShape() {
    actual val size: Vec3f = Vec3f(size)
    override val shape: btCollisionShape

    init {
        Physics.checkIsLoaded()

        val halfExtents = size.scale(0.5f, MutableVec3f())
        shape = Ammo.btBoxShape(halfExtents.toBtVector3())
    }
}