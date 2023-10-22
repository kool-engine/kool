package de.fabmax.kool.math.spatial

import de.fabmax.kool.math.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * A simple axis-aligned bounding box
 *
 * @author fabmax
 */
open class BoundingBox() {

    private val mutMin = MutableVec3f()
    private val mutMax = MutableVec3f()
    private val mutSize = MutableVec3f()
    private val mutCenter = MutableVec3f()

    var isEmpty = true
        private set

    val isNotEmpty: Boolean get() = !isEmpty

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
            size.mul(0.5f, mutCenter).add(min)
        }
    }

    private fun addPoint(point: Vec3f) = addPoint(point.x, point.y, point.z)

    private fun addPoint(x: Float, y: Float, z: Float) {
        if (isEmpty) {
            mutMin.set(x, y, z)
            mutMax.set(x, y, z)
            isEmpty = false
        } else {
            if (x < min.x) { mutMin.x = x }
            if (y < min.y) { mutMin.y = y }
            if (z < min.z) { mutMin.z = z }
            if (x > max.x) { mutMax.x = x }
            if (y > max.y) { mutMax.y = y }
            if (z > max.z) { mutMax.z = z }
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

    fun add(point: Vec3f, radius: Float): BoundingBox {
        addPoint(point.x - radius, point.y - radius, point.z - radius)
        addPoint(point.x + radius, point.y + radius, point.z + radius)
        updateSizeAndCenter()
        return this
    }

    fun expand(e: Vec3f): BoundingBox {
        if (isEmpty) {
            throw IllegalStateException("Empty BoundingBox cannot be expanded")
        }
        mutMin -= e
        mutMax += e
        updateSizeAndCenter()
        return this
    }

    fun signedExpand(e: Vec3f): BoundingBox {
        if (isEmpty) {
            throw IllegalStateException("Empty BoundingBox cannot be expanded")
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

    fun move(offset: Vec3f) = move(offset.x, offset.y, offset.z)

    fun move(x: Float, y: Float, z: Float): BoundingBox {
        if (isEmpty) {
            throw IllegalStateException("Empty BoundingBox cannot be moved")
        }
        mutMin.x += x
        mutMin.y += y
        mutMin.z += z
        mutMax.x += x
        mutMax.y += y
        mutMax.z += z
        mutCenter.x += x
        mutCenter.y += y
        mutCenter.z += z
        return this
    }

    fun setMerged(aabb1: BoundingBox, aabb2: BoundingBox): BoundingBox {
        // manual if is faster than min() and max()
        mutMin.x = min(aabb1.min.x, aabb2.min.x)
        mutMin.y = min(aabb1.min.y, aabb2.min.y)
        mutMin.z = min(aabb1.min.z, aabb2.min.z)

        mutMax.x = max(aabb1.max.x, aabb2.max.x)
        mutMax.y = max(aabb1.max.y, aabb2.max.y)
        mutMax.z = max(aabb1.max.z, aabb2.max.z)

        isEmpty = false
        updateSizeAndCenter()
        return this
    }

    operator fun contains(point: Vec3f): Boolean {
        return point.x >= min.x && point.x <= max.x &&
                point.y >= min.y && point.y <= max.y &&
                point.z >= min.z && point.z <= max.z
    }

    fun contains(x: Float, y: Float, z: Float): Boolean {
        return x >= min.x && x <= max.x &&
                y >= min.y && y <= max.y &&
                z >= min.z && z <= max.z
    }

    operator fun contains(aabb: BoundingBox): Boolean {
        return aabb.min in this && aabb.max in this
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

    fun clampToBounds(point: MutableVec3d) {
        point.x = point.x.clamp(min.x.toDouble(), max.x.toDouble())
        point.y = point.y.clamp(min.y.toDouble(), max.y.toDouble())
        point.z = point.z.clamp(min.z.toDouble(), max.z.toDouble())
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
        if (pt in this) {
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
        if (ray.origin in this) {
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
            // no intersection: box is behind ray
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
