package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f

class RaycastResult {
    val hitPosition = MutableVec3f()
    val hitNormal = MutableVec3f()
    var hitDistance = -1f
    var hitActor: RigidActor? = null

    val isHit: Boolean
        get() = hitActor != null

    fun clear() {
        hitPosition.set(Vec3f.ZERO)
        hitNormal.set(Vec3f.ZERO)
        hitDistance = -1f
        hitActor = null
    }
}