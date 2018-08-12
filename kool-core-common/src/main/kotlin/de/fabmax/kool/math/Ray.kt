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

    fun nearestPointOnRay(point: Vec3f, result: MutableVec3f): MutableVec3f {
        val d = (point.dot(direction) - origin.dot(direction)) / direction.dot(direction)
        if (d > 0) {
            result.set(direction).scale(d).add(origin)
        } else {
            result.set(origin)
        }
        return result
    }

    fun distanceToPoint(point: Vec3f): Float = sqrt(sqrDistanceToPoint(point))

    fun sqrDistanceToPoint(point: Vec3f): Float = sqrDistanceToPoint(point.x, point.y, point.z)

    fun sqrDistanceToPoint(x: Float, y: Float, z: Float): Float {
        val nx: Float
        val ny: Float
        val nz: Float
        val dot = x * direction.x + y * direction.y + z * direction.z
        val d = (dot - origin.dot(direction)) / direction.dot(direction)
        if (d > 0) {
            nx = direction.x * d + origin.x - x
            ny = direction.y * d + origin.y - y
            nz = direction.z * d + origin.z - z
        } else {
            nx = origin.x - x
            ny = origin.y - y
            nz = origin.z - z
        }
        return nx*nx + ny*ny + nz*nz
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
