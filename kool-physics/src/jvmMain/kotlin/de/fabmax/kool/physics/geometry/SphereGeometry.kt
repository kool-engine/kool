package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.Physics
import physx.geometry.PxSphereGeometry

actual class SphereGeometry actual constructor(radius: Float) : CommonSphereGeometry(radius), CollisionGeometry {

    override val pxGeometry: PxSphereGeometry

    init {
        Physics.checkIsLoaded()
        pxGeometry = PxSphereGeometry(radius)
    }

    actual override fun release() = pxGeometry.destroy()
}