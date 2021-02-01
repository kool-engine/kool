package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.Vec3f
import physx.geomutils.PxBoxGeometry

actual class BoxGeometry actual constructor(size: Vec3f) : CommonBoxGeometry(size), CollisionGeometry {

    override val pxGeometry = PxBoxGeometry(size.x * 0.5f, size.y * 0.5f, size.z * 0.5f)

}