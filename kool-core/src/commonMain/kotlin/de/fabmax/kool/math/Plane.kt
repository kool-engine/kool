package de.fabmax.kool.math

/**
 * @author fabmax
 */
class Plane() {

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
    fun intersectionPoint(ray: Ray, result: MutableVec3f): Boolean {
        val denom = n.dot(ray.direction)
        if (!denom.isFuzzyZero()) {
            val t = p.subtract(ray.origin, result).dot(n) / denom
            result.set(ray.direction).scale(t).add(ray.origin)
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

    fun toVec4() : MutableVec4f = toVec4(MutableVec4f())

    fun toVec4(result: MutableVec4f): MutableVec4f {
        result.x = n.x
        result.y = n.y
        result.z = n.z
        result.w = n.dot(p)
        return result
    }
}
