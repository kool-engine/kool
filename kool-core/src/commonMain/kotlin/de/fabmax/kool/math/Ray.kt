package de.fabmax.kool.math

import de.fabmax.kool.scene.Node
import kotlin.math.sqrt

/**
 * @author fabmax
 */

class Ray() {
    val origin = MutableVec3f()
    val direction = MutableVec3f()

    constructor(origin: Vec3f, direction: Vec3f) : this() {
        this.origin.set(origin)
        this.direction.set(direction)
    }

    fun set(other: Ray) {
        origin.set(other.origin)
        direction.set(other.direction)
    }

    fun setFromLookAt(origin: Vec3f, lookAt: Vec3f) {
        this.origin.set(origin)
        direction.set(lookAt).subtract(origin).norm()
    }

    fun distanceToPoint(point: Vec3f): Float = point.distanceToRay(origin, direction)

    fun sqrDistanceToPoint(point: Vec3f): Float = point.sqrDistanceToRay(origin, direction)

    fun sqrDistanceToPoint(x: Float, y: Float, z: Float) = sqrDistancePointToRay(x, y, z, origin, direction)

    fun sphereIntersection(center: Vec3f, radius: Float, result: MutableVec3f): Boolean {
        result.set(origin).subtract(center)
        val a = direction.dot(direction)
        val b = result.dot(direction) * 2f
        val c = result.dot(result) - radius * radius
        val discr = b * b - 4 * a * c

        if (discr < 0f) {
            return false
        }

        val numerator = -b - sqrt(discr)
        if (numerator > 0f) {
            val d = numerator / (2f * a)
            result.set(direction).mul(d).add(origin)
            return true
        }

        val numerator2 = -b + sqrt(discr)
        if (numerator2 > 0f) {
            val d = numerator2 / (2f * a)
            result.set(direction).mul(d).add(origin)
            return true
        }

        return false
    }

    fun transformBy(matrix: Mat4f, result: Ray = this): Ray {
        matrix.transform(origin, 1f, result.origin)
        matrix.transform(direction, 0f, result.direction).norm()
        return result
    }

    fun transformBy(matrix: Mat4d, result: Ray = this): Ray {
        matrix.transform(origin, 1f, result.origin)
        matrix.transform(direction, 0f, result.direction).norm()
        return result
    }

    override fun toString(): String {
        return "{origin=$origin, direction=$direction}"
    }
}

class RayTest {
    val ray = Ray()

    val hitPositionGlobal = MutableVec3f()
    val hitNormalGlobal = MutableVec3f()

    private val tmpRay = Ray()
    private val tmpHitPoint = MutableVec3f()

    var hitNode: Node? = null
        private set
    var hitDistanceSqr = Float.MAX_VALUE
        private set
    val isHit: Boolean
        get() = hitDistanceSqr < Float.MAX_VALUE

    fun clear() {
        hitPositionGlobal.set(Vec3f.ZERO)
        hitNormalGlobal.set(Vec3f.ZERO)
        hitNode = null
        hitDistanceSqr = Float.MAX_VALUE
    }

    fun setHit(node: Node, hitDistanceGlobal: Float, hitNormalGlobal: Vec3f? = null) {
        hitPositionGlobal.set(ray.direction).mul(hitDistanceGlobal).add(ray.origin)
        setHit(node, hitPositionGlobal, hitNormalGlobal)
    }

    fun setHit(node: Node, hitPositionGlobal: Vec3f, hitNormalGlobal: Vec3f? = null) {
        this.hitPositionGlobal.set(hitPositionGlobal)
        this.hitNormalGlobal.set(hitNormalGlobal ?: Vec3f.ZERO)
        hitNode = node
        hitDistanceSqr = this.hitPositionGlobal.sqrDistance(ray.origin)
    }

    /**
     * Returns true if this [ray] hits the [node]'s bounding sphere AND the hit is closer than any previous hit.
     */
    fun isIntersectingBoundingSphere(node: Node) = isIntersectingBoundingSphere(node.globalCenter, node.globalRadius)

    /**
     * Returns true if this [ray] hits the specified bounding sphere (in global coordinates) AND the hit is closer than
     * any previous hit.
     */
    fun isIntersectingBoundingSphere(globalCenter: Vec3f, globalRadius: Float): Boolean {
        val isSphereHit = ray.sphereIntersection(globalCenter, globalRadius, tmpHitPoint)
        return isSphereHit && tmpHitPoint.sqrDistance(ray.origin) <= hitDistanceSqr
    }

    fun getRayTransformed(matrix: Mat4f): Ray {
        return ray.transformBy(matrix, tmpRay)
    }

    fun getRayTransformed(matrix: Mat4d): Ray {
        return ray.transformBy(matrix, tmpRay)
    }
}
