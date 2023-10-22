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

    fun transformBy(matrix: Mat4f) {
        matrix.transform(origin)
        matrix.transform(direction, 0f).norm()
    }

    fun transformBy(matrix: Mat4d) {
        matrix.transform(origin)
        matrix.transform(direction, 0f).norm()
    }

    override fun toString(): String {
        return "{origin=$origin, direction=$direction}"
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
        intHitPosition.set(ray.direction).mul(distance).add(ray.origin)
        setHit(node, intHitPosition)
    }

    fun setHit(node: Node, position: Vec3f) {
        intHitPosition.set(position)
        intHitPositionLocal.set(position)
        hitNode = node
        hitDistanceSqr = hitPosition.sqrDistance(ray.origin)
    }

    fun transformBy(matrix: Mat4f) {
        ray.transformBy(matrix)
        if (isHit) {
            matrix.transform(intHitPosition)
            hitDistanceSqr = hitPosition.sqrDistance(ray.origin)
        }
    }

    fun transformBy(matrix: Mat4d) {
        ray.transformBy(matrix)
        if (isHit) {
            matrix.transform(intHitPosition)
            hitDistanceSqr = hitPosition.sqrDistance(ray.origin)
        }
    }
}
