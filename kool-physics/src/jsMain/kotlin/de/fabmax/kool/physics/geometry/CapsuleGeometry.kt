package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.Physics
import physx.PxCapsuleGeometry
import physx.PxGeometry

actual class CapsuleGeometry actual constructor(height: Float, radius: Float) : CommonCapsuleGeometry(height, radius), CollisionGeometry {

    override val pxGeometry: PxGeometry

    init {
        Physics.checkIsLoaded()
        pxGeometry = PxCapsuleGeometry(radius, height / 2f)
    }
}