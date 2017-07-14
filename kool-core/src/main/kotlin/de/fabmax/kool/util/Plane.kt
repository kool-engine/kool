package de.fabmax.kool.util

import de.fabmax.kool.platform.Math

/**
 * @author fabmax
 */
class Plane {

    /**
     * Some point in the plane.
     */
    val p = MutableVec3f()

    /**
     * Normal vector of the plane.
     */
    val n = MutableVec3f(Vec3f.Y_AXIS)

    /**
     * Computes the intersection point of this plane and the specified ray. Returns false if there is no intersection
     * point (i.e. plane and ray are parallel).
     */
    fun intersectionPoint(result: MutableVec3f, ray: Ray): Boolean {
        val denom = n.dot(ray.direction)
        if (!Math.Companion.isZero(denom)) {
            val t = p.subtract_(ray.origin, result).dot(n) / denom
            result.set(ray.direction).scale(t).add(ray.origin)
            return t >= 0
        }
        return false
    }

}
