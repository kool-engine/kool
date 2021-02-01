package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import physx.PxBoxGeometry
import physx.PxGeometry

actual class BoxGeometry actual constructor(size: Vec3f) : CommonBoxGeometry(size), CollisionGeometry {

    override val pxGeometry: PxGeometry

    init {
        Physics.checkIsLoaded()
        pxGeometry = PxBoxGeometry(size.x * 0.5f, size.y * 0.5f, size.z * 0.5f)
    }
}