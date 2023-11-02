package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.Physics
import physx.geometry.PxCapsuleGeometry

actual class CapsuleGeometry actual constructor(height: Float, radius: Float) : CommonCapsuleGeometry(height, radius), CollisionGeometry {

    override val pxGeometry: PxCapsuleGeometry

    init {
        Physics.checkIsLoaded()
        pxGeometry = PxCapsuleGeometry(radius, height / 2f)
    }

    actual override fun release() = pxGeometry.destroy()
}