package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f

class HitResult {
    val hitPosition = MutableVec3f()
    val hitNormal = MutableVec3f()
    var hitDistance = -1f
    var nearestActor: RigidActor? = null
    val hitActors = mutableListOf<RigidActor>()

    val isHit: Boolean
        get() = nearestActor != null

    fun clear() {
        hitPosition.set(Vec3f.ZERO)
        hitNormal.set(Vec3f.ZERO)
        hitDistance = -1f
        nearestActor = null
        hitActors.clear()
    }
}