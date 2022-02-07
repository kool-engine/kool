package de.fabmax.kool.math.spatial

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f

interface ItemAdapter<in T: Any> {
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

object Vec3fAdapter : ItemAdapter<Vec3f> {
    override fun getMinX(item: Vec3f): Float = item.x
    override fun getMinY(item: Vec3f): Float = item.y
    override fun getMinZ(item: Vec3f): Float = item.z

    override fun getMaxX(item: Vec3f): Float = item.x
    override fun getMaxY(item: Vec3f): Float = item.y
    override fun getMaxZ(item: Vec3f): Float = item.z

    override fun getCenterX(item: Vec3f): Float = item.x
    override fun getCenterY(item: Vec3f): Float = item.y
    override fun getCenterZ(item: Vec3f): Float = item.z

    override fun getSzX(item: Vec3f): Float = 0f
    override fun getSzY(item: Vec3f): Float = 0f
    override fun getSzZ(item: Vec3f): Float = 0f

    override fun getMin(item: Vec3f, result: MutableVec3f): MutableVec3f = result.set(item)
    override fun getCenter(item: Vec3f, result: MutableVec3f): MutableVec3f = result.set(item)
    override fun getMax(item: Vec3f, result: MutableVec3f): MutableVec3f = result.set(item)
}

object EdgeAdapter : ItemAdapter<Edge<*>> {
    override fun getMinX(item: Edge<*>): Float = item.minX
    override fun getMinY(item: Edge<*>): Float = item.minY
    override fun getMinZ(item: Edge<*>): Float = item.minZ

    override fun getMaxX(item: Edge<*>): Float = item.maxX
    override fun getMaxY(item: Edge<*>): Float = item.maxY
    override fun getMaxZ(item: Edge<*>): Float = item.maxZ

    override fun getMin(item: Edge<*>, result: MutableVec3f): MutableVec3f =
        result.set(item.minX, item.minY, item.minZ)
    override fun getMax(item: Edge<*>, result: MutableVec3f): MutableVec3f =
        result.set(item.maxX, item.maxY, item.maxZ)
}

object TriangleAdapter : ItemAdapter<Triangle> {
    override fun getMinX(item: Triangle): Float = item.minX
    override fun getMinY(item: Triangle): Float = item.minY
    override fun getMinZ(item: Triangle): Float = item.minZ

    override fun getMaxX(item: Triangle): Float = item.maxX
    override fun getMaxY(item: Triangle): Float = item.maxY
    override fun getMaxZ(item: Triangle): Float = item.maxZ

    override fun getMin(item: Triangle, result: MutableVec3f): MutableVec3f =
        result.set(item.minX, item.minY, item.minZ)
    override fun getMax(item: Triangle, result: MutableVec3f): MutableVec3f =
        result.set(item.maxX, item.maxY, item.maxZ)
}

object BoundingBoxAdapter : ItemAdapter<BoundingBox> {
    override fun getMinX(item: BoundingBox): Float = item.min.x
    override fun getMinY(item: BoundingBox): Float = item.min.y
    override fun getMinZ(item: BoundingBox): Float = item.min.z

    override fun getMaxX(item: BoundingBox): Float = item.max.x
    override fun getMaxY(item: BoundingBox): Float = item.max.y
    override fun getMaxZ(item: BoundingBox): Float = item.max.z

    override fun getCenterX(item: BoundingBox): Float = item.center.x
    override fun getCenterY(item: BoundingBox): Float = item.center.y
    override fun getCenterZ(item: BoundingBox): Float = item.center.z

    override fun getSzX(item: BoundingBox): Float = item.size.x
    override fun getSzY(item: BoundingBox): Float = item.size.y
    override fun getSzZ(item: BoundingBox): Float = item.size.z

    override fun getMin(item: BoundingBox, result: MutableVec3f): MutableVec3f = result.set(item.min)
    override fun getCenter(item: BoundingBox, result: MutableVec3f): MutableVec3f = result.set(item.center)
    override fun getMax(item: BoundingBox, result: MutableVec3f): MutableVec3f = result.set(item.max)
}
