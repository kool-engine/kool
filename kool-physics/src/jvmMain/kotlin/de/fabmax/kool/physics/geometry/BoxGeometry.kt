package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import physx.geometry.PxBoxGeometry

actual class BoxGeometry actual constructor(size: Vec3f) : CommonBoxGeometry(size), CollisionGeometry {

    override val pxGeometry: PxBoxGeometry

    init {
        Physics.checkIsLoaded()
        pxGeometry = PxBoxGeometry(size.x * 0.5f, size.y * 0.5f, size.z * 0.5f)
    }

    actual override fun release() = pxGeometry.destroy()
}