package de.fabmax.kool.math.spatial

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f

interface ItemAdapter<T: Any> {
    fun getMinX(item: T): Float
    fun getMinY(item: T): Float
    fun getMinZ(item: T): Float

    fun getMaxX(item: T): Float
    fun getMaxY(item: T): Float
    fun getMaxZ(item: T): Float

    fun getCenterX(item: T): Float = (getMinX(item) + getMaxX(item)) * 0.5f
    fun getCenterY(item: T): Float = (getMinY(item) + getMaxY(item)) * 0.5f
    fun getCenterZ(item: T): Float = (getMinZ(item) + getMaxZ(item)) * 0.5f

    fun getSzX(item: T): Float = getMaxX(item) - getMinX(item)
    fun getSzY(item: T): Float = getMaxY(item) - getMinY(item)
    fun getSzZ(item: T): Float = getMaxZ(item) - getMinZ(item)

    fun getMin(item: T, result: MutableVec3f): MutableVec3f =
            result.set(getMinX(item), getMinY(item), getMinZ(item))
    fun getMax(item: T, result: MutableVec3f): MutableVec3f =
            result.set(getMaxX(item), getMaxY(item), getMaxZ(item))
    fun getCenter(item: T, result: MutableVec3f): MutableVec3f =
            result.set(getCenterX(item), getCenterY(item), getCenterZ(item))

    fun setNode(item: T, node: SpatialTree<T>.Node) { }
}

class Vec3fAdapter<T: Vec3f> : ItemAdapter<T> {
    override fun getMinX(item: T): Float = item.x
    override fun getMinY(item: T): Float = item.y
    override fun getMinZ(item: T): Float = item.z

    override fun getMaxX(item: T): Float = item.x
    override fun getMaxY(item: T): Float = item.y
    override fun getMaxZ(item: T): Float = item.z

    override fun getCenterX(item: T): Float = item.x
    override fun getCenterY(item: T): Float = item.y
    override fun getCenterZ(item: T): Float = item.z

    override fun getSzX(item: T): Float = 0f
    override fun getSzY(item: T): Float = 0f
    override fun getSzZ(item: T): Float = 0f

    override fun getMin(item: T, result: MutableVec3f): MutableVec3f = result.set(item)
    override fun getCenter(item: T, result: MutableVec3f): MutableVec3f = result.set(item)
    override fun getMax(item: T, result: MutableVec3f): MutableVec3f = result.set(item)
}

class EdgeAdapter<T: Edge<*>> : ItemAdapter<T> {
    override fun getMinX(item: T): Float = item.minX
    override fun getMinY(item: T): Float = item.minY
    override fun getMinZ(item: T): Float = item.minZ

    override fun getMaxX(item: T): Float = item.maxX
    override fun getMaxY(item: T): Float = item.maxY
    override fun getMaxZ(item: T): Float = item.maxZ

    override fun getMin(item: T, result: MutableVec3f): MutableVec3f =
        result.set(item.minX, item.minY, item.minZ)
    override fun getMax(item: T, result: MutableVec3f): MutableVec3f =
        result.set(item.maxX, item.maxY, item.maxZ)
}

class TriangleAdapter<T: Triangle> : ItemAdapter<T> {
    override fun getMinX(item: T): Float = item.minX
    override fun getMinY(item: T): Float = item.minY
    override fun getMinZ(item: T): Float = item.minZ

    override fun getMaxX(item: T): Float = item.maxX
    override fun getMaxY(item: T): Float = item.maxY
    override fun getMaxZ(item: T): Float = item.maxZ

    override fun getMin(item: T, result: MutableVec3f): MutableVec3f =
        result.set(item.minX, item.minY, item.minZ)
    override fun getMax(item: T, result: MutableVec3f): MutableVec3f =
        result.set(item.maxX, item.maxY, item.maxZ)
}

class BoundingBoxAdapter<T: BoundingBox> : ItemAdapter<T> {
    override fun getMinX(item: T): Float = item.min.x
    override fun getMinY(item: T): Float = item.min.y
    override fun getMinZ(item: T): Float = item.min.z

    override fun getMaxX(item: T): Float = item.max.x
    override fun getMaxY(item: T): Float = item.max.y
    override fun getMaxZ(item: T): Float = item.max.z

    override fun getCenterX(item: T): Float = item.center.x
    override fun getCenterY(item: T): Float = item.center.y
    override fun getCenterZ(item: T): Float = item.center.z

    override fun getSzX(item: T): Float = item.size.x
    override fun getSzY(item: T): Float = item.size.y
    override fun getSzZ(item: T): Float = item.size.z

    override fun getMin(item: T, result: MutableVec3f): MutableVec3f = result.set(item.min)
    override fun getCenter(item: T, result: MutableVec3f): MutableVec3f = result.set(item.center)
    override fun getMax(item: T, result: MutableVec3f): MutableVec3f = result.set(item.max)
}
