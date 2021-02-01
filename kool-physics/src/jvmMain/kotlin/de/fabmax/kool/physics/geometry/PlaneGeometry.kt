package de.fabmax.kool.physics.geometry

import physx.geomutils.PxPlaneGeometry

actual class PlaneGeometry : CommonPlaneGeometry(), CollisionGeometry {

    override val pxGeometry = PxPlaneGeometry()

}