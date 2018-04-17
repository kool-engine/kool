package de.fabmax.kool.math

import de.fabmax.kool.scene.Node
import kotlin.math.sqrt

/**
 * @author fabmax
 */

class Ray {
    val origin = MutableVec3f()
    val direction = MutableVec3f()

    fun set(other: Ray) {
        origin.set(other.origin)
        direction.set(other.direction)
    }

    fun setFromLookAt(origin: Vec3f, lookAt: Vec3f) {
        this.origin.set(origin)
        direction.set(lookAt).subtract(origin).norm()
    }

    fun nearestPointOnRay(result: MutableVec3f, point: Vec3f) {
        val d = (point.dot(direction) - origin.dot(direction)) / direction.dot(direction)
        if (d > 0) {
            result.set(direction).scale(d).add(origin)
        } else {
            result.set(origin)
        }
    }
}

class RayTest {

    val ray = Ray()

    val hitPosition = MutableVec3f()
    val hitPositionLocal = MutableVec3f()
    var hitNode: Node? = null
    var hitDistanceSqr = Float.MAX_VALUE
    val isHit: Boolean
        get() = hitDistanceSqr < Float.MAX_VALUE

    fun clear() {
        hitPosition.set(Vec3f.ZERO)
        hitPositionLocal.set(Vec3f.ZERO)
        hitNode = null
        hitDistanceSqr = Float.MAX_VALUE
    }

    fun computeHitPosition() {
        if (isHit) {
            val dist = sqrt(hitDistanceSqr.toDouble()).toFloat()
            hitPosition.set(ray.direction).norm().scale(dist).add(ray.origin)
        }
    }
}
