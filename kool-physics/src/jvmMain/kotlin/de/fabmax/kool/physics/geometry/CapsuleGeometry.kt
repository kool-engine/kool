package de.fabmax.kool.physics.geometry

import physx.geomutils.PxCapsuleGeometry

actual class CapsuleGeometry actual constructor(height: Float, radius: Float) : CommonCapsuleGeometry(height, radius), CollisionGeometry {

    override val pxGeometry = PxCapsuleGeometry(radius, height / 2f)

}