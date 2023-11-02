package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.Physics
import physx.geometry.PxPlaneGeometry

actual class PlaneGeometry : CommonPlaneGeometry(), CollisionGeometry {

    override val pxGeometry: PxPlaneGeometry

    init {
        Physics.checkIsLoaded()
        pxGeometry = PxPlaneGeometry()
    }

    actual override fun release() = pxGeometry.destroy()
}