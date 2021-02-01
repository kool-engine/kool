package de.fabmax.kool.physics.geometry

import physx.geomutils.PxSphereGeometry

@Suppress("CanBeParameter")
actual class SphereGeometry actual constructor(radius: Float) : CommonSphereGeometry(radius), CollisionGeometry {

    override val pxGeometry = PxSphereGeometry(radius)

}