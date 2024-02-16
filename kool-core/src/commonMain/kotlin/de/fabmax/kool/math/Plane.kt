package de.fabmax.kool.math

fun PlaneF.toPlaneD(result: PlaneD = PlaneD()): PlaneD {
    p.toMutableVec3d(result.p)
    n.toMutableVec3d(result.n)
    return result
}

fun PlaneD.toPlaneF(result: PlaneF = PlaneF()): PlaneF {
    p.toMutableVec3f(result.p)
    n.toMutableVec3f(result.n)
    return result
}

// <template> Changes made within the template section will also affect the other type variants of this class

/**
 * Plane in normal representation.
 */
class PlaneF() {

    /**
     * Some point in the plane.
     */
    val p = MutableVec3f()

    /**
     * Normal vector of the plane.
     */
    val n = MutableVec3f(Vec3f.Y_AXIS)

    constructor(p: Vec3f, n: Vec3f) : this() {
        this.p.set(p)
        this.n.set(n)
    }

    /**
     * Computes the intersection point of this plane and the specified ray. Returns false if there is no intersection
     * point (i.e. plane and ray are parallel).
     */
    fun intersectionPoint(ray: RayF, result: MutableVec3f): Boolean {
        val denom = n.dot(ray.direction)
        if (!denom.isFuzzyZero()) {
            val t = p.subtract(ray.origin, result).dot(n) / denom
            result.set(ray.direction).mul(t).add(ray.origin)
            return t >= 0
        }
        return false
    }

    /**
     * Computes the signed distance of the given point to this plane. Positive distance means that the point
     * is in front of the plane (positive normal direction), negative distance means it is behind the plane (negative
     * normal direction).
     */
    fun distance(point: Vec3f): Float {
        return n.dot(point) - n.dot(p)
    }

    fun toVec4(): MutableVec4f = toVec4(MutableVec4f())

    fun toVec4(result: MutableVec4f): MutableVec4f {
        result.x = n.x
        result.y = n.y
        result.z = n.z
        result.w = n.dot(p)
        return result
    }
}

// </template> End of template section, DO NOT EDIT BELOW THIS!


/**
 * Plane in normal representation.
 */
class PlaneD() {

    /**
     * Some point in the plane.
     */
    val p = MutableVec3d()

    /**
     * Normal vector of the plane.
     */
    val n = MutableVec3d(Vec3d.Y_AXIS)

    constructor(p: Vec3d, n: Vec3d) : this() {
        this.p.set(p)
        this.n.set(n)
    }

    /**
     * Computes the intersection point of this plane and the specified ray. Returns false if there is no intersection
     * point (i.e. plane and ray are parallel).
     */
    fun intersectionPoint(ray: RayD, result: MutableVec3d): Boolean {
        val denom = n.dot(ray.direction)
        if (!denom.isFuzzyZero()) {
            val t = p.subtract(ray.origin, result).dot(n) / denom
            result.set(ray.direction).mul(t).add(ray.origin)
            return t >= 0
        }
        return false
    }

    /**
     * Computes the signed distance of the given point to this plane. Positive distance means that the point
     * is in front of the plane (positive normal direction), negative distance means it is behind the plane (negative
     * normal direction).
     */
    fun distance(point: Vec3d): Double {
        return n.dot(point) - n.dot(p)
    }

    fun toVec4(): MutableVec4d = toVec4(MutableVec4d())

    fun toVec4(result: MutableVec4d): MutableVec4d {
        result.x = n.x
        result.y = n.y
        result.z = n.z
        result.w = n.dot(p)
        return result
    }
}
