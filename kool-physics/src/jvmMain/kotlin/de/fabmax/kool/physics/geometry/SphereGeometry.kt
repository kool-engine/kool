package de.fabmax.kool.physics.geometry

import physx.geomutils.PxSphereGeometry

actual class SphereGeometry actual constructor(radius: Float) : CommonSphereGeometry(radius), CollisionGeometry {

    override val pxGeometry = PxSphereGeometry(radius)

}