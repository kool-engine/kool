package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.Physics
import physx.PxGeometry
import physx.PxSphereGeometry

actual class SphereGeometry actual constructor(radius: Float) : CommonSphereGeometry(radius), CollisionGeometry {

    override val pxGeometry: PxGeometry

    init {
        Physics.checkIsLoaded()
        pxGeometry = PxSphereGeometry(radius)
    }
}