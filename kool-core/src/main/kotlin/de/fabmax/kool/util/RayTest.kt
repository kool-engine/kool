package de.fabmax.kool.util

import de.fabmax.kool.scene.Node

/**
 * @author fabmax
 */

class RayTest {

    val origin = MutableVec3f()
    val direction = MutableVec3f()

    val hitPosition = MutableVec3f()
    var hitNode: Node? = null
    var hitDistanceSqr = Float.POSITIVE_INFINITY
    val isHit: Boolean
        get() = hitDistanceSqr < Float.POSITIVE_INFINITY

    fun clear() {
        hitPosition.set(Vec3f.ZERO)
        hitNode = null
        hitDistanceSqr = Float.POSITIVE_INFINITY
    }

    fun computeHitPosition() {
        if (isHit) {
            hitPosition.set(direction).scale(Math.sqrt(hitDistanceSqr.toDouble()).toFloat()).add(origin)
        }
    }
}
