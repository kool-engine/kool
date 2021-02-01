package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.Physics
import physx.PxGeometry
import physx.PxPlaneGeometry

actual class PlaneGeometry : CommonPlaneGeometry(), CollisionGeometry {
    override val pxGeometry: PxGeometry

    init {
        Physics.checkIsLoaded()
        pxGeometry = PxPlaneGeometry()
    }
}