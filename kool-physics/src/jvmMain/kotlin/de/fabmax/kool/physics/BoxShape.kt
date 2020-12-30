package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics.toVector3f

actual class BoxShape actual constructor(size: Vec3f) : CollisionShape() {
    actual val size: Vec3f = Vec3f(size)
    override val shape: btCollisionShape

    init {
        val halfExtents = size.scale(0.5f, MutableVec3f())
        shape = btBoxShape(halfExtents.toVector3f())
    }
}