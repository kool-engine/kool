package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btBoxShape
import ammo.toBtVector3
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics

actual class BoxShape actual constructor(size: Vec3f) : CommonBoxShape(size), CollisionShape {

    override val btShape: btBoxShape

    init {
        Physics.checkIsLoaded()

        val halfExtents = size.scale(0.5f, MutableVec3f())
        btShape = Ammo.btBoxShape(halfExtents.toBtVector3())
    }
}