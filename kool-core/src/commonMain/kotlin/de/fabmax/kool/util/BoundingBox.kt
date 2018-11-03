package de.fabmax.kool.util

import de.fabmax.kool.KoolException
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

    var isBatchUpdate = false
        set(value) {
            field = value
            if (!value) {
                updateSizeAndCenter()
            }
        }

    constructor(min: Vec3f, max: Vec3f): this() {
        set(min, max)
    }

    private fun updateSizeAndCenter() {
        if (!isBatchUpdate) {
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

    inline fun batchUpdate(block: BoundingBox.() -> Unit) {
        val wasBatchUpdate = isBatchUpdate
        isBatchUpdate = true
        block()
        isBatchUpdate = wasBatchUpdate
    }

    fun isFuzzyEqual(other: BoundingBox): Boolean {
        return isEmpty == other.isEmpty && min.isFuzzyEqual(other.min) && max.isFuzzyEqual(other.max)
    }

    fun clear(): BoundingBox {
        isEmpty = true
        mutMin.set(Vec3f.ZERO)
        mutMax.set(Vec3f.ZERO)
        updateSizeAndCenter()
        return this
    }

    fun add(point: Vec3f): BoundingBox {
        addPoint(point)
        updateSizeAndCenter()
        return this
    }

    fun add(points: List<Vec3f>): BoundingBox {
        add(points, points.indices)
        return this
    }

    fun add(points: List<Vec3f>, range: IntRange): BoundingBox {
        for (i in range) {
            addPoint(points[i])
        }
        updateSizeAndCenter()
        return this
    }

    fun add(aabb: BoundingBox): BoundingBox {
        if (!aabb.isEmpty) {
            addPoint(aabb.min)
            addPoint(aabb.max)
            updateSizeAndCenter()
        }
        return this
    }

    fun expand(e: Vec3f): BoundingBox {
        if (isEmpty) {
            throw KoolException("Empty BoundingBox cannot be expanded")
        }
        mutMin -= e
        mutMax += e
        updateSizeAndCenter()
        return this
    }

    fun signedExpand(e: Vec3f): BoundingBox {
        if (isEmpty) {
            throw KoolException("Empty BoundingBox cannot be expanded")
        }
        if (e.x > 0) mutMax.x += e.x else mutMin.x += e.x
        if (e.y > 0) mutMax.y += e.y else mutMin.y += e.y
        if (e.z > 0) mutMax.z += e.z else mutMin.z += e.z
        updateSizeAndCenter()
        return this
    }

    fun set(other: BoundingBox): BoundingBox {
        mutMin.set(other.min)
        mutMax.set(other.max)
        mutSize.set(other.size)
        mutCenter.set(other.center)
        isEmpty = other.isEmpty
        return this
    }

    fun set(min: Vec3f, max: Vec3f): BoundingBox {
        isEmpty = false
        mutMin.set(min)
        mutMax.set(max)
        updateSizeAndCenter()
        return this
    }

    fun set(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float): BoundingBox {
        isEmpty = false
        mutMin.set(minX, minY, minZ)
        mutMax.set(maxX, maxY, maxZ)
        updateSizeAndCenter()
        return this
    }

    fun setMerged(aabb1: BoundingBox, aabb2: BoundingBox): BoundingBox {
        // manual if is faster than min() and max()
        mutMin.x = if (aabb1.min.x < aabb2.min.x) { aabb1.min.x } else { aabb2.min.x }
        mutMin.y = if (aabb1.min.y < aabb2.min.y) { aabb1.min.y } else { aabb2.min.y }
        mutMin.z = if (aabb1.min.z < aabb2.min.z) { aabb1.min.z } else { aabb2.min.z }

        mutMax.x = if (aabb1.max.x > aabb2.max.x) { aabb1.max.x } else { aabb2.max.x }
        mutMax.y = if (aabb1.max.y > aabb2.max.y) { aabb1.max.y } else { aabb2.max.y }
        mutMax.z = if (aabb1.max.z > aabb2.max.z) { aabb1.max.z } else { aabb2.max.z }

        isEmpty = false
        updateSizeAndCenter()
        return this
    }

    fun isIncluding(point: Vec3f): Boolean {
        return point.x >= min.x && point.x <= max.x &&
                point.y >= min.y && point.y <= max.y &&
                point.z >= min.z && point.z <= max.z
    }

    fun isIncluding(x: Float, y: Float, z: Float): Boolean {
        return x >= min.x && x <= max.x &&
                y >= min.y && y <= max.y &&
                z >= min.z && z <= max.z
    }

    fun isIncluding(aabb: BoundingBox): Boolean {
        return isIncluding(aabb.min) && isIncluding(aabb.max)
    }

    fun isIntersecting(aabb: BoundingBox): Boolean {
        return min.x <= aabb.max.x && max.x >= aabb.min.x &&
                min.y <= aabb.max.y && max.y >= aabb.min.y &&
                min.z <= aabb.max.z && max.z >= aabb.min.z
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
     * Float.MAX_VALUE is returned. If the ray origin is inside this BoundingBox 0 is returned.
     * The method returns the squared distance because it's faster to compute. If the exact distance is needed
     * the square root of the result has to be taken.
     *
     * @param ray    The ray to test
     * @return squared distance between origin and the hit point on the BoundingBox surface or
     * Float.MAX_VALUE if the ray does not intersects the BoundingBox
     */
    fun hitDistanceSqr(ray: Ray): Float {
        var tmin: Float
        var tmax: Float
        val tymin: Float
        val tymax: Float
        val tzmin: Float
        val tzmax: Float

        if (isEmpty) {
            return Float.MAX_VALUE
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
            return Float.MAX_VALUE
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
            return Float.MAX_VALUE
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
            return Float.MAX_VALUE
        }
    }

    override fun toString(): String {
        return if (isEmpty) {
            "[empty]"
        } else {
            "[min=$min, max=$max]"
        }
    }

    /**
     * Checks aabb components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BoundingBox) return false

        if (isEmpty != other.isEmpty) return false
        if (min != other.min) return false
        if (max != other.max) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isEmpty.hashCode()
        result = 31 * result + min.hashCode()
        result = 31 * result + max.hashCode()
        return result
    }
}
