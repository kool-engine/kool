package de.fabmax.kool.math.spatial

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toMutableVec3d

interface ItemAdapter<T: Any> {
    fun getMinX(item: T): Double
    fun getMinY(item: T): Double
    fun getMinZ(item: T): Double

    fun getMaxX(item: T): Double
    fun getMaxY(item: T): Double
    fun getMaxZ(item: T): Double

    fun getCenterX(item: T): Double = (getMinX(item) + getMaxX(item)) * 0.5f
    fun getCenterY(item: T): Double = (getMinY(item) + getMaxY(item)) * 0.5f
    fun getCenterZ(item: T): Double = (getMinZ(item) + getMaxZ(item)) * 0.5f

    fun getSzX(item: T): Double = getMaxX(item) - getMinX(item)
    fun getSzY(item: T): Double = getMaxY(item) - getMinY(item)
    fun getSzZ(item: T): Double = getMaxZ(item) - getMinZ(item)

    fun getMin(item: T, result: MutableVec3d): MutableVec3d =
            result.set(getMinX(item), getMinY(item), getMinZ(item))
    fun getMax(item: T, result: MutableVec3d): MutableVec3d =
            result.set(getMaxX(item), getMaxY(item), getMaxZ(item))
    fun getCenter(item: T, result: MutableVec3d): MutableVec3d =
            result.set(getCenterX(item), getCenterY(item), getCenterZ(item))

    fun setNode(item: T, node: SpatialTree<T>.Node) { }
}

class Vec3fAdapter<T: Vec3f> : ItemAdapter<T> {
    override fun getMinX(item: T): Double = item.x.toDouble()
    override fun getMinY(item: T): Double = item.y.toDouble()
    override fun getMinZ(item: T): Double = item.z.toDouble()

    override fun getMaxX(item: T): Double = item.x.toDouble()
    override fun getMaxY(item: T): Double = item.y.toDouble()
    override fun getMaxZ(item: T): Double = item.z.toDouble()

    override fun getCenterX(item: T): Double = item.x.toDouble()
    override fun getCenterY(item: T): Double = item.y.toDouble()
    override fun getCenterZ(item: T): Double = item.z.toDouble()

    override fun getSzX(item: T): Double = 0.0
    override fun getSzY(item: T): Double = 0.0
    override fun getSzZ(item: T): Double = 0.0

    override fun getMin(item: T, result: MutableVec3d): MutableVec3d = item.toMutableVec3d(result)
    override fun getCenter(item: T, result: MutableVec3d): MutableVec3d = item.toMutableVec3d(result)
    override fun getMax(item: T, result: MutableVec3d): MutableVec3d = item.toMutableVec3d(result)
}

class Vec3dAdapter<T: Vec3d> : ItemAdapter<T> {
    override fun getMinX(item: T): Double = item.x
    override fun getMinY(item: T): Double = item.y
    override fun getMinZ(item: T): Double = item.z

    override fun getMaxX(item: T): Double = item.x
    override fun getMaxY(item: T): Double = item.y
    override fun getMaxZ(item: T): Double = item.z

    override fun getCenterX(item: T): Double = item.x
    override fun getCenterY(item: T): Double = item.y
    override fun getCenterZ(item: T): Double = item.z

    override fun getSzX(item: T): Double = 0.0
    override fun getSzY(item: T): Double = 0.0
    override fun getSzZ(item: T): Double = 0.0

    override fun getMin(item: T, result: MutableVec3d): MutableVec3d = result.set(item)
    override fun getCenter(item: T, result: MutableVec3d): MutableVec3d = result.set(item)
    override fun getMax(item: T, result: MutableVec3d): MutableVec3d = result.set(item)
}

class EdgeAdapter<T: Edge<*>> : ItemAdapter<T> {
    override fun getMinX(item: T): Double = item.minX.toDouble()
    override fun getMinY(item: T): Double = item.minY.toDouble()
    override fun getMinZ(item: T): Double = item.minZ.toDouble()

    override fun getMaxX(item: T): Double = item.maxX.toDouble()
    override fun getMaxY(item: T): Double = item.maxY.toDouble()
    override fun getMaxZ(item: T): Double = item.maxZ.toDouble()

    override fun getMin(item: T, result: MutableVec3d): MutableVec3d =
        result.set(item.minX.toDouble(), item.minY.toDouble(), item.minZ.toDouble())
    override fun getMax(item: T, result: MutableVec3d): MutableVec3d =
        result.set(item.maxX.toDouble(), item.maxY.toDouble(), item.maxZ.toDouble())
}

class TriangleAdapter<T: Triangle> : ItemAdapter<T> {
    override fun getMinX(item: T): Double = item.minX.toDouble()
    override fun getMinY(item: T): Double = item.minY.toDouble()
    override fun getMinZ(item: T): Double = item.minZ.toDouble()

    override fun getMaxX(item: T): Double = item.maxX.toDouble()
    override fun getMaxY(item: T): Double = item.maxY.toDouble()
    override fun getMaxZ(item: T): Double = item.maxZ.toDouble()

    override fun getMin(item: T, result: MutableVec3d): MutableVec3d =
        result.set(item.minX.toDouble(), item.minY.toDouble(), item.minZ.toDouble())
    override fun getMax(item: T, result: MutableVec3d): MutableVec3d =
        result.set(item.maxX.toDouble(), item.maxY.toDouble(), item.maxZ.toDouble())
}

class BoundingBoxAdapter<T: BoundingBoxF> : ItemAdapter<T> {
    override fun getMinX(item: T): Double = item.min.x.toDouble()
    override fun getMinY(item: T): Double = item.min.y.toDouble()
    override fun getMinZ(item: T): Double = item.min.z.toDouble()

    override fun getMaxX(item: T): Double = item.max.x.toDouble()
    override fun getMaxY(item: T): Double = item.max.y.toDouble()
    override fun getMaxZ(item: T): Double = item.max.z.toDouble()

    override fun getCenterX(item: T): Double = item.center.x.toDouble()
    override fun getCenterY(item: T): Double = item.center.y.toDouble()
    override fun getCenterZ(item: T): Double = item.center.z.toDouble()

    override fun getSzX(item: T): Double = item.size.x.toDouble()
    override fun getSzY(item: T): Double = item.size.y.toDouble()
    override fun getSzZ(item: T): Double = item.size.z.toDouble()

    override fun getMin(item: T, result: MutableVec3d): MutableVec3d = item.min.toMutableVec3d(result)
    override fun getCenter(item: T, result: MutableVec3d): MutableVec3d = item.center.toMutableVec3d(result)
    override fun getMax(item: T, result: MutableVec3d): MutableVec3d = item.max.toMutableVec3d(result)
}
