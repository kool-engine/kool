package de.fabmax.kool.math.spatial

import de.fabmax.kool.math.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

fun BoundingBoxF.toBoundingBoxD(result: BoundingBoxD = BoundingBoxD()): BoundingBoxD {
    result.clear()
    if (isNotEmpty) {
        result.add(min.toVec3d()).add(max.toVec3d())
    }
    return result
}

fun BoundingBoxD.toBoundingBoxF(result: BoundingBoxF = BoundingBoxF()): BoundingBoxF {
    result.clear()
    if (isNotEmpty) {
        result.add(min.toVec3f()).add(max.toVec3f())
    }
    return result
}

operator fun BoundingBoxD.contains(point: Vec3f): Boolean {
    return isNotEmpty &&
            point.x >= min.x && point.x <= max.x &&
            point.y >= min.y && point.y <= max.y &&
            point.z >= min.z && point.z <= max.z
}

// <template> Changes made within the template section will also affect the other type variants of this class

/**
 * A simple axis-aligned bounding box.
 */
open class BoundingBoxF() {

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

    @PublishedApi
    internal var isBatchUpdate = false

    constructor(min: Vec3f, max: Vec3f): this() {
        set(min, max)
    }

    operator fun component1(): Vec3f = min
    operator fun component2(): Vec3f = max

    @PublishedApi
    internal fun updateSizeAndCenter() {
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

    inline fun batchUpdate(block: BoundingBoxF.() -> Unit) {
        isBatchUpdate = true
        block()
        isBatchUpdate = false
        updateSizeAndCenter()
    }

    fun isFuzzyEqual(other: BoundingBoxF): Boolean {
        return isEmpty == other.isEmpty && min.isFuzzyEqual(other.min) && max.isFuzzyEqual(other.max)
    }

    fun clear(): BoundingBoxF {
        isEmpty = true
        mutMin.set(Vec3f.ZERO)
        mutMax.set(Vec3f.ZERO)
        updateSizeAndCenter()
        return this
    }

    fun add(point: Vec3f): BoundingBoxF {
        addPoint(point)
        updateSizeAndCenter()
        return this
    }

    fun add(points: List<Vec3f>): BoundingBoxF {
        add(points, points.indices)
        return this
    }

    fun add(points: List<Vec3f>, range: IntRange): BoundingBoxF {
        for (i in range) {
            addPoint(points[i])
        }
        updateSizeAndCenter()
        return this
    }

    fun add(aabb: BoundingBoxF): BoundingBoxF {
        if (!aabb.isEmpty) {
            addPoint(aabb.min)
            addPoint(aabb.max)
            updateSizeAndCenter()
        }
        return this
    }

    fun add(point: Vec3f, radius: Float): BoundingBoxF {
        addPoint(point.x - radius, point.y - radius, point.z - radius)
        addPoint(point.x + radius, point.y + radius, point.z + radius)
        updateSizeAndCenter()
        return this
    }

    fun expand(e: Vec3f): BoundingBoxF {
        if (isEmpty) {
            throw IllegalStateException("Empty BoundingBox cannot be expanded")
        }
        mutMin -= e
        mutMax += e
        updateSizeAndCenter()
        return this
    }

    fun signedExpand(e: Vec3f): BoundingBoxF {
        if (isEmpty) {
            throw IllegalStateException("Empty BoundingBox cannot be expanded")
        }
        if (e.x > 0) mutMax.x += e.x else mutMin.x += e.x
        if (e.y > 0) mutMax.y += e.y else mutMin.y += e.y
        if (e.z > 0) mutMax.z += e.z else mutMin.z += e.z
        updateSizeAndCenter()
        return this
    }

    fun set(other: BoundingBoxF): BoundingBoxF {
        mutMin.set(other.min)
        mutMax.set(other.max)
        mutSize.set(other.size)
        mutCenter.set(other.center)
        isEmpty = other.isEmpty
        return this
    }

    fun set(min: Vec3f, max: Vec3f): BoundingBoxF {
        isEmpty = false
        mutMin.set(min)
        mutMax.set(max)
        updateSizeAndCenter()
        return this
    }

    fun set(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float): BoundingBoxF {
        isEmpty = false
        mutMin.set(minX, minY, minZ)
        mutMax.set(maxX, maxY, maxZ)
        updateSizeAndCenter()
        return this
    }

    fun move(offset: Vec3f) = move(offset.x, offset.y, offset.z)

    fun move(x: Float, y: Float, z: Float): BoundingBoxF {
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

    fun setMerged(aabb1: BoundingBoxF, aabb2: BoundingBoxF): BoundingBoxF {
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
        return isNotEmpty &&
                point.x >= min.x && point.x <= max.x &&
                point.y >= min.y && point.y <= max.y &&
                point.z >= min.z && point.z <= max.z
    }

    fun contains(x: Float, y: Float, z: Float): Boolean {
        return isNotEmpty &&
                x >= min.x && x <= max.x &&
                y >= min.y && y <= max.y &&
                z >= min.z && z <= max.z
    }

    operator fun contains(aabb: BoundingBoxF): Boolean {
        return isNotEmpty && aabb.isNotEmpty && aabb.min in this && aabb.max in this
    }

    fun isIntersecting(aabb: BoundingBoxF): Boolean {
        return isNotEmpty && aabb.isNotEmpty &&
                min.x <= aabb.max.x && max.x >= aabb.min.x &&
                min.y <= aabb.max.y && max.y >= aabb.min.y &&
                min.z <= aabb.max.z && max.z >= aabb.min.z
    }

    fun clampToBounds(point: MutableVec3f) {
        if (isNotEmpty) {
            point.x = point.x.clamp(min.x, max.x)
            point.y = point.y.clamp(min.y, max.y)
            point.z = point.z.clamp(min.z, max.z)
        }
    }

    /**
     * Computes the distance between the given point and this BoundingBox. It this BoundingBox includes
     * the point, 0 is returned.
     */
    fun pointDistance(pt: Vec3f): Float {
        if (isEmpty) {
            return Float.POSITIVE_INFINITY
        }
        return sqrt(pointDistanceSqr(pt))
    }

    /**
     * Computes the squared distance between the given point and this BoundingBox. It this BoundingBox includes
     * the point, 0 is returned.
     */
    fun pointDistanceSqr(pt: Vec3f): Float {
        if (isEmpty) {
            return Float.POSITIVE_INFINITY
        }
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
     * Float.POSITIVE_INFINITY is returned. If the ray origin is inside this BoundingBox 0 is returned.
     * The method returns the squared distance because it's faster to compute. If the exact distance is needed
     * the square root of the result has to be taken.
     *
     * @param ray    The ray to test
     * @return squared distance between origin and the hit point on the BoundingBox surface or
     * Float.POSITIVE_INFINITY if the ray does not intersect the BoundingBox
     */
    fun hitDistanceSqr(ray: RayF): Float {
        var tmin: Float
        var tmax: Float
        val tymin: Float
        val tymax: Float
        val tzmin: Float
        val tzmax: Float

        if (isEmpty) {
            return Float.POSITIVE_INFINITY
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

        return if (tmin > 0) {
            // hit! calculate square distance between ray origin and hit point
            var comp = ray.direction.x * tmin
            var dist = comp * comp
            comp = ray.direction.y * tmin
            dist += comp * comp
            comp = ray.direction.z * tmin
            dist += comp * comp
            dist / ray.direction.sqrLength()
        } else {
            // no intersection: box is behind ray
            Float.POSITIVE_INFINITY
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
        if (other !is BoundingBoxF) return false

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

// </template> End of template section, DO NOT EDIT BELOW THIS!


/**
 * A simple axis-aligned bounding box.
 */
open class BoundingBoxD() {

    private val mutMin = MutableVec3d()
    private val mutMax = MutableVec3d()
    private val mutSize = MutableVec3d()
    private val mutCenter = MutableVec3d()

    var isEmpty = true
        private set

    val isNotEmpty: Boolean get() = !isEmpty

    val min: Vec3d = mutMin
    val max: Vec3d = mutMax
    val size: Vec3d = mutSize
    val center: Vec3d = mutCenter

    @PublishedApi
    internal var isBatchUpdate = false

    constructor(min: Vec3d, max: Vec3d): this() {
        set(min, max)
    }

    operator fun component1(): Vec3d = min
    operator fun component2(): Vec3d = max

    @PublishedApi
    internal fun updateSizeAndCenter() {
        if (!isBatchUpdate) {
            // size = max - min
            mutMax.subtract(mutMin, mutSize)
            // center = min + size * 0.5
            size.mul(0.5, mutCenter).add(min)
        }
    }

    private fun addPoint(point: Vec3d) = addPoint(point.x, point.y, point.z)

    private fun addPoint(x: Double, y: Double, z: Double) {
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

    inline fun batchUpdate(block: BoundingBoxD.() -> Unit) {
        isBatchUpdate = true
        block()
        isBatchUpdate = false
        updateSizeAndCenter()
    }

    fun isFuzzyEqual(other: BoundingBoxD): Boolean {
        return isEmpty == other.isEmpty && min.isFuzzyEqual(other.min) && max.isFuzzyEqual(other.max)
    }

    fun clear(): BoundingBoxD {
        isEmpty = true
        mutMin.set(Vec3d.ZERO)
        mutMax.set(Vec3d.ZERO)
        updateSizeAndCenter()
        return this
    }

    fun add(point: Vec3d): BoundingBoxD {
        addPoint(point)
        updateSizeAndCenter()
        return this
    }

    fun add(points: List<Vec3d>): BoundingBoxD {
        add(points, points.indices)
        return this
    }

    fun add(points: List<Vec3d>, range: IntRange): BoundingBoxD {
        for (i in range) {
            addPoint(points[i])
        }
        updateSizeAndCenter()
        return this
    }

    fun add(aabb: BoundingBoxD): BoundingBoxD {
        if (!aabb.isEmpty) {
            addPoint(aabb.min)
            addPoint(aabb.max)
            updateSizeAndCenter()
        }
        return this
    }

    fun add(point: Vec3d, radius: Double): BoundingBoxD {
        addPoint(point.x - radius, point.y - radius, point.z - radius)
        addPoint(point.x + radius, point.y + radius, point.z + radius)
        updateSizeAndCenter()
        return this
    }

    fun expand(e: Vec3d): BoundingBoxD {
        if (isEmpty) {
            throw IllegalStateException("Empty BoundingBox cannot be expanded")
        }
        mutMin -= e
        mutMax += e
        updateSizeAndCenter()
        return this
    }

    fun signedExpand(e: Vec3d): BoundingBoxD {
        if (isEmpty) {
            throw IllegalStateException("Empty BoundingBox cannot be expanded")
        }
        if (e.x > 0) mutMax.x += e.x else mutMin.x += e.x
        if (e.y > 0) mutMax.y += e.y else mutMin.y += e.y
        if (e.z > 0) mutMax.z += e.z else mutMin.z += e.z
        updateSizeAndCenter()
        return this
    }

    fun set(other: BoundingBoxD): BoundingBoxD {
        mutMin.set(other.min)
        mutMax.set(other.max)
        mutSize.set(other.size)
        mutCenter.set(other.center)
        isEmpty = other.isEmpty
        return this
    }

    fun set(min: Vec3d, max: Vec3d): BoundingBoxD {
        isEmpty = false
        mutMin.set(min)
        mutMax.set(max)
        updateSizeAndCenter()
        return this
    }

    fun set(minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double): BoundingBoxD {
        isEmpty = false
        mutMin.set(minX, minY, minZ)
        mutMax.set(maxX, maxY, maxZ)
        updateSizeAndCenter()
        return this
    }

    fun move(offset: Vec3d) = move(offset.x, offset.y, offset.z)

    fun move(x: Double, y: Double, z: Double): BoundingBoxD {
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

    fun setMerged(aabb1: BoundingBoxD, aabb2: BoundingBoxD): BoundingBoxD {
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

    operator fun contains(point: Vec3d): Boolean {
        return isNotEmpty &&
                point.x >= min.x && point.x <= max.x &&
                point.y >= min.y && point.y <= max.y &&
                point.z >= min.z && point.z <= max.z
    }

    fun contains(x: Double, y: Double, z: Double): Boolean {
        return isNotEmpty &&
                x >= min.x && x <= max.x &&
                y >= min.y && y <= max.y &&
                z >= min.z && z <= max.z
    }

    operator fun contains(aabb: BoundingBoxD): Boolean {
        return isNotEmpty && aabb.isNotEmpty && aabb.min in this && aabb.max in this
    }

    fun isIntersecting(aabb: BoundingBoxD): Boolean {
        return isNotEmpty && aabb.isNotEmpty &&
                min.x <= aabb.max.x && max.x >= aabb.min.x &&
                min.y <= aabb.max.y && max.y >= aabb.min.y &&
                min.z <= aabb.max.z && max.z >= aabb.min.z
    }

    fun clampToBounds(point: MutableVec3d) {
        if (isNotEmpty) {
            point.x = point.x.clamp(min.x, max.x)
            point.y = point.y.clamp(min.y, max.y)
            point.z = point.z.clamp(min.z, max.z)
        }
    }

    /**
     * Computes the distance between the given point and this BoundingBox. It this BoundingBox includes
     * the point, 0 is returned.
     */
    fun pointDistance(pt: Vec3d): Double {
        if (isEmpty) {
            return Double.POSITIVE_INFINITY
        }
        return sqrt(pointDistanceSqr(pt))
    }

    /**
     * Computes the squared distance between the given point and this BoundingBox. It this BoundingBox includes
     * the point, 0 is returned.
     */
    fun pointDistanceSqr(pt: Vec3d): Double {
        if (isEmpty) {
            return Double.POSITIVE_INFINITY
        }
        if (pt in this) {
            return 0.0
        }

        var x = 0.0
        var y = 0.0
        var z = 0.0

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
     * Double.POSITIVE_INFINITY is returned. If the ray origin is inside this BoundingBox 0 is returned.
     * The method returns the squared distance because it's faster to compute. If the exact distance is needed
     * the square root of the result has to be taken.
     *
     * @param ray    The ray to test
     * @return squared distance between origin and the hit point on the BoundingBox surface or
     * Double.POSITIVE_INFINITY if the ray does not intersect the BoundingBox
     */
    fun hitDistanceSqr(ray: RayD): Double {
        var tmin: Double
        var tmax: Double
        val tymin: Double
        val tymax: Double
        val tzmin: Double
        val tzmax: Double

        if (isEmpty) {
            return Double.POSITIVE_INFINITY
        }
        if (ray.origin in this) {
            return 0.0
        }

        var div = 1.0 / ray.direction.x
        if (div >= 0.0) {
            tmin = (min.x - ray.origin.x) * div
            tmax = (max.x - ray.origin.x) * div
        } else {
            tmin = (max.x - ray.origin.x) * div
            tmax = (min.x - ray.origin.x) * div
        }

        div = 1.0 / ray.direction.y
        if (div >= 0.0) {
            tymin = (min.y - ray.origin.y) * div
            tymax = (max.y - ray.origin.y) * div
        } else {
            tymin = (max.y - ray.origin.y) * div
            tymax = (min.y - ray.origin.y) * div
        }

        if (tmin > tymax || tymin > tmax) {
            // no intersection
            return Double.POSITIVE_INFINITY
        }
        if (tymin > tmin) {
            tmin = tymin
        }
        if (tymax < tmax) {
            tmax = tymax
        }

        div = 1.0 / ray.direction.z
        if (div >= 0.0) {
            tzmin = (min.z - ray.origin.z) * div
            tzmax = (max.z - ray.origin.z) * div
        } else {
            tzmin = (max.z - ray.origin.z) * div
            tzmax = (min.z - ray.origin.z) * div
        }

        if (tmin > tzmax || tzmin > tmax) {
            // no intersection
            return Double.POSITIVE_INFINITY
        }
        if (tzmin > tmin) {
            tmin = tzmin
        }

        return if (tmin > 0) {
            // hit! calculate square distance between ray origin and hit point
            var comp = ray.direction.x * tmin
            var dist = comp * comp
            comp = ray.direction.y * tmin
            dist += comp * comp
            comp = ray.direction.z * tmin
            dist += comp * comp
            dist / ray.direction.sqrLength()
        } else {
            // no intersection: box is behind ray
            Double.POSITIVE_INFINITY
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
        if (other !is BoundingBoxD) return false

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
