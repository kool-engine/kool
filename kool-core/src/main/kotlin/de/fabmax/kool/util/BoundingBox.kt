package de.fabmax.kool.util


/**
 * A simple axis-aligned bounding box
 *
 * @author fabmax
 */
class BoundingBox {

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

    private fun updateSizeCenter() {
        // size = max - min
        mutMax.subtract(mutSize, mutMin)
        // center = min + size * 0.5
        size.scale(mutCenter, 0.5f).add(min)
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

    fun clear() {
        isEmpty = true
        mutMin.set(Vec3f.ZERO)
        mutMax.set(Vec3f.ZERO)
        updateSizeCenter()
    }

    fun add(point: Vec3f) {
        addPoint(point)
        updateSizeCenter()
    }

    fun add(points: List<Vec3f>) {
        for (i in points.indices) {
            addPoint(points[i])
        }
        updateSizeCenter()
    }

    fun add(aabb: BoundingBox) {
        if (!aabb.isEmpty) {
            addPoint(aabb.min)
            addPoint(aabb.max)
            updateSizeCenter()
        }
    }

    fun set(other: BoundingBox) {
        mutMin.set(other.min)
        mutMax.set(other.max)
        mutSize.set(other.size)
        mutCenter.set(other.center)
        isEmpty = other.isEmpty
    }

    fun isIncluding(point: Vec3f): Boolean {
        return point.x >= min.x && point.x <= max.x &&
                point.y >= min.y && point.y <= max.y &&
                point.z >= min.z && point.z <= max.z
    }

    /**
     * Computes the squared hit distance for the given ray. If the ray does not intersect this BoundingBox
     * [Float.POSITIVE_INFINITY] is returned. If the ray origin is inside this BoundingBox 0 is returned.
     * The method returns the squared distance because it's faster to compute. If the exact distance is needed
     * the square root of the result has to be taken.
     *
     * @param ray    The ray to test
     * @return squared distance between origin and the hit point on the BoundingBox surface or
     * [Float.POSITIVE_INFINITY] if the ray does not intersects the BoundingBox
     */
    fun computeHitDistanceSqr(ray: Ray): Float {
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
            return "[center=$center, size=$size]"
        }
    }
}