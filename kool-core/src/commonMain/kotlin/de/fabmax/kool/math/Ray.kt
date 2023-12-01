package de.fabmax.kool.math

import kotlin.math.sqrt

fun RayF.toRayD(result: RayD = RayD()): RayD {
    origin.toMutableVec3d(result.origin)
    direction.toMutableVec3d(result.direction)
    return result
}

fun RayD.toRayF(result: RayF = RayF()): RayF {
    origin.toMutableVec3f(result.origin)
    direction.toMutableVec3f(result.direction)
    return result
}

fun RayF.transformBy(matrix: Mat4d, result: RayF = this): RayF {
    matrix.transform(origin, 1f, result.origin)
    matrix.transform(direction, 0f, result.direction).norm()
    return result
}

// <template> Changes made within the template section will also affect the other type variants of this class

class RayF() {
    val origin = MutableVec3f()
    val direction = MutableVec3f()

    constructor(origin: Vec3f, direction: Vec3f) : this() {
        this.origin.set(origin)
        this.direction.set(direction)
    }

    fun set(other: RayF) {
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

    fun transformBy(matrix: Mat4f, result: RayF = this): RayF {
        matrix.transform(origin, 1f, result.origin)
        matrix.transform(direction, 0f, result.direction).norm()
        return result
    }

    override fun toString(): String {
        return "{origin=$origin, direction=$direction}"
    }
}

// </template> End of template section, DO NOT EDIT BELOW THIS!


class RayD() {
    val origin = MutableVec3d()
    val direction = MutableVec3d()

    constructor(origin: Vec3d, direction: Vec3d) : this() {
        this.origin.set(origin)
        this.direction.set(direction)
    }

    fun set(other: RayD) {
        origin.set(other.origin)
        direction.set(other.direction)
    }

    fun setFromLookAt(origin: Vec3d, lookAt: Vec3d) {
        this.origin.set(origin)
        direction.set(lookAt).subtract(origin).norm()
    }

    fun distanceToPoint(point: Vec3d): Double = point.distanceToRay(origin, direction)

    fun sqrDistanceToPoint(point: Vec3d): Double = point.sqrDistanceToRay(origin, direction)

    fun sqrDistanceToPoint(x: Double, y: Double, z: Double) = sqrDistancePointToRay(x, y, z, origin, direction)

    fun sphereIntersection(center: Vec3d, radius: Double, result: MutableVec3d): Boolean {
        result.set(origin).subtract(center)
        val a = direction.dot(direction)
        val b = result.dot(direction) * 2.0
        val c = result.dot(result) - radius * radius
        val discr = b * b - 4 * a * c

        if (discr < 0.0) {
            return false
        }

        val numerator = -b - sqrt(discr)
        if (numerator > 0.0) {
            val d = numerator / (2.0 * a)
            result.set(direction).mul(d).add(origin)
            return true
        }

        val numerator2 = -b + sqrt(discr)
        if (numerator2 > 0.0) {
            val d = numerator2 / (2.0 * a)
            result.set(direction).mul(d).add(origin)
            return true
        }

        return false
    }

    fun transformBy(matrix: Mat4d, result: RayD = this): RayD {
        matrix.transform(origin, 1.0, result.origin)
        matrix.transform(direction, 0.0, result.direction).norm()
        return result
    }

    override fun toString(): String {
        return "{origin=$origin, direction=$direction}"
    }
}
