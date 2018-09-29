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
        val d = (point * direction - origin * direction) / (direction * direction)
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
        val d = (dot - origin * direction) / (direction * direction)
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

    private val intHitPosition = MutableVec3f()
    private val intHitPositionLocal = MutableVec3f()

    val hitPosition: Vec3f get() = intHitPosition
    val hitPositionLocal : Vec3f get() = intHitPositionLocal
    var hitNode: Node? = null
        private set
    var hitDistanceSqr = Float.MAX_VALUE
        private set
    val isHit: Boolean
        get() = hitDistanceSqr < Float.MAX_VALUE

    fun clear() {
        intHitPosition.set(Vec3f.ZERO)
        intHitPositionLocal.set(Vec3f.ZERO)
        hitNode = null
        hitDistanceSqr = Float.MAX_VALUE
    }

    fun setHit(node: Node, distance: Float) {
        intHitPosition.set(ray.direction).scale(distance).add(ray.origin)
        setHit(node, intHitPosition)
    }

    fun setHit(node: Node, position: Vec3f) {
        intHitPosition.set(position)
        intHitPositionLocal.set(position)
        hitNode = node
        hitDistanceSqr = hitPosition.sqrDistance(ray.origin)
    }

    fun transformBy(matrix: Mat4f) {
        matrix.transform(ray.origin)
        matrix.transform(ray.direction, 0f)
        ray.direction.norm()
        if (isHit) {
            matrix.transform(intHitPosition)
            hitDistanceSqr = hitPosition.sqrDistance(ray.origin)
        }
    }
}
