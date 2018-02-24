package de.fabmax.kool.util

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Ray
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.clamp
import kotlin.math.sqrt

/**
 * A simple axis-aligned bounding box
 *
 * @author fabmax
 */
class BoundingBox() {

    private val mutMin = MutableVec3f()
    private val mutMax = MutableVec3f()
    private val mutSize = MutableVec3f()
    private val mutCenter = MutableVec3f()

    var isEmpty = true
        private set

    val min: Vec3f = mutMin
    val max: Vec3f = mutMax
    val size: Vec3f = mutSize
    val center: Vec3f = mutCenter

    var batchUpdate = false
        set(value) {
            field = value
            updateSizeAndCenter()
        }

    constructor(min: Vec3f, max: Vec3f): this() {
        mutMin.set(min)
        mutMax.set(max)
        updateSizeAndCenter()
    }

    private fun updateSizeAndCenter() {
        if (!batchUpdate) {
            // size = max - min
            mutMax.subtract(mutMin, mutSize)
            // center = min + size * 0.5
            size.scale(0.5f, mutCenter).add(min)
        }
    }

    private fun addPoint(point: Vec3f) {
        if (isEmpty) {
            mutMin.set(point)
            mutMax.set(point)
            isEmpty = false
        } else {
            if (point.x < min.x) { mutMin.x = point.x }
            if (point.y < min.y) { mutMin.y = point.y }
            if (point.z < min.z) { mutMin.z = point.z }
            if (point.x > max.x) { mutMax.x = point.x }
            if (point.y > max.y) { mutMax.y = point.y }
            if (point.z > max.z) { mutMax.z = point.z }
        }
    }

    fun isEqual(other: BoundingBox): Boolean {
        return isEmpty == other.isEmpty && min.isEqual(other.min) && max.isEqual(other.max)
    }

    fun clear() {
        isEmpty = true
        mutMin.set(Vec3f.ZERO)
        mutMax.set(Vec3f.ZERO)
        updateSizeAndCenter()
    }

    fun add(point: Vec3f) {
        addPoint(point)
        updateSizeAndCenter()
    }

    fun add(points: List<Vec3f>) {
        add(points, points.indices)
    }

    fun add(points: List<Vec3f>, range: IntRange) {
        for (i in range) {
            addPoint(points[i])
        }
        updateSizeAndCenter()
    }

    fun add(aabb: BoundingBox) {
        if (!aabb.isEmpty) {
            addPoint(aabb.min)
            addPoint(aabb.max)
            updateSizeAndCenter()
        }
    }

    fun set(other: BoundingBox) {
        mutMin.set(other.min)
        mutMax.set(other.max)
        mutSize.set(other.size)
        mutCenter.set(other.center)
        isEmpty = other.isEmpty
    }

    fun set(min: Vec3f, max: Vec3f) {
        isEmpty = false
        mutMin.set(min)
        mutMax.set(max)
        updateSizeAndCenter()
    }

    fun set(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float) {
        isEmpty = false
        mutMin.set(minX, minY, minZ)
        mutMax.set(maxX, maxY, maxZ)
        updateSizeAndCenter()
    }

    fun isIncluding(point: Vec3f): Boolean {
        return point.x >= min.x && point.x <= max.x &&
                point.y >= min.y && point.y <= max.y &&
                point.z >= min.z && point.z <= max.z
    }

    fun clampToBounds(point: MutableVec3f) {
        point.x = point.x.clamp(min.x, max.x)
        point.y = point.y.clamp(min.y, max.y)
        point.z = point.z.clamp(min.z, max.z)
    }

    /**
     * Computes the distance between the given point and this BoundingBox. It this BoundingBox includes
     * the point, 0 is returned.
     */
    fun pointDistance(pt: Vec3f): Float {
        return sqrt(pointDistanceSqr(pt).toDouble()).toFloat()
    }

    /**
     * Computes the squared distance between the given point and this BoundingBox. It this BoundingBox includes
     * the point, 0 is returned.
     */
    fun pointDistanceSqr(pt: Vec3f): Float {
        if (isIncluding(pt)) {
            return 0f
        }

        var x = 0.0f
        var y = 0.0f
        var z = 0.0f

        var tmp = pt.x - min.x
        if (tmp < 0) {
            // px < minX
            x = tmp
        } else {
            tmp = max.x - pt.x
            if (tmp < 0) {
                // px > maxX
                x = tmp
            }
        }

        tmp = pt.y - min.y
        if (tmp < 0) {
            // py < minY
            y = tmp
        } else {
            tmp = max.y - pt.y
            if (tmp < 0) {
                // py > maxY
                y = tmp
            }
        }

        tmp = pt.z - min.z
        if (tmp < 0) {
            // pz < minZ
            z = tmp
        } else {
            tmp = max.z - pt.z
            if (tmp < 0) {
                // pz > maxZ
                z = tmp
            }
        }

        return x*x + y*y + z*z
    }

    /**
     * Computes the squared hit distance for the given ray. If the ray does not intersect this BoundingBox
     * Float.POSITIVE_INFINITY is returned. If the ray origin is inside this BoundingBox 0 is returned.
     * The method returns the squared distance because it's faster to compute. If the exact distance is needed
     * the square root of the result has to be taken.
     *
     * @param ray    The ray to test
     * @return squared distance between origin and the hit point on the BoundingBox surface or
     * Float.POSITIVE_INFINITY if the ray does not intersects the BoundingBox
     */
    fun hitDistanceSqr(ray: Ray): Float {
        var tmin: Float
        var tmax: Float
        val tymin: Float
        val tymax: Float
        val tzmin: Float
        val tzmax: Float

        if (isEmpty) {
            return Float.POSITIVE_INFINITY
        }
        if (isIncluding(ray.origin)) {
            return 0f
        }

        var div = 1.0f / ray.direction.x
        if (div >= 0.0f) {
            tmin = (min.x - ray.origin.x) * div
            tmax = (max.x - ray.origin.x) * div
        } else {
            tmin = (max.x - ray.origin.x) * div
            tmax = (min.x - ray.origin.x) * div
        }

        div = 1.0f / ray.direction.y
        if (div >= 0.0f) {
            tymin = (min.y - ray.origin.y) * div
            tymax = (max.y - ray.origin.y) * div
        } else {
            tymin = (max.y - ray.origin.y) * div
            tymax = (min.y - ray.origin.y) * div
        }

        if (tmin > tymax || tymin > tmax) {
            // no intersection
            return Float.POSITIVE_INFINITY
        }
        if (tymin > tmin) {
            tmin = tymin
        }
        if (tymax < tmax) {
            tmax = tymax
        }

        div = 1.0f / ray.direction.z
        if (div >= 0.0f) {
            tzmin = (min.z - ray.origin.z) * div
            tzmax = (max.z - ray.origin.z) * div
        } else {
            tzmin = (max.z - ray.origin.z) * div
            tzmax = (min.z - ray.origin.z) * div
        }

        if (tmin > tzmax || tzmin > tmax) {
            // no intersection
            return Float.POSITIVE_INFINITY
        }
        if (tzmin > tmin) {
            tmin = tzmin
        }

        if (tmin > 0) {
            // hit! calculate square distance between ray origin and hit point
            var comp = ray.direction.x * tmin
            var dist = comp * comp
            comp = ray.direction.y * tmin
            dist += comp * comp
            comp = ray.direction.z * tmin
            dist += comp * comp
            return dist / ray.direction.sqrLength()
        } else {
            // no intersection
            return Float.POSITIVE_INFINITY
        }
    }

    override fun toString(): String {
        if (isEmpty) {
            return "[empty]"
        } else {
            return "[min=$min, max=$max]"
        }
    }
}