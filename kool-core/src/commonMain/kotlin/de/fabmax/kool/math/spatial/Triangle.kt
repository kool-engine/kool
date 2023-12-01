package de.fabmax.kool.math.spatial

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.RayF
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.PrimitiveType

open class Triangle(val pt0: Vec3f, val pt1: Vec3f, val pt2: Vec3f) {
    val e1: Vec3f
    val e2: Vec3f

    val minX: Float
    val minY: Float
    val minZ: Float
    val maxX: Float
    val maxY: Float
    val maxZ: Float

    private val tmpS = MutableVec3f()
    private val tmpP = MutableVec3f()
    private val tmpQ = MutableVec3f()

    constructor(data: IndexedVertexList, idx0: Int) : this(
            MutableVec3f().apply { data.vertexIt.index = data.indices[idx0]; set(data.vertexIt.position) },
            MutableVec3f().apply { data.vertexIt.index = data.indices[idx0+1]; set(data.vertexIt.position) },
            MutableVec3f().apply { data.vertexIt.index = data.indices[idx0+2]; set(data.vertexIt.position) }
    ) {
        if (data.primitiveType != PrimitiveType.TRIANGLES) {
            throw IllegalArgumentException("Supplied geometry data must have primitiveType TRIANGLES")
        }
    }

    init {
        e1 = pt1.subtract(pt0, MutableVec3f())
        e2 = pt2.subtract(pt0, MutableVec3f())

        minX = minOf(pt0.x, pt1.x, pt2.x)
        minY = minOf(pt0.y, pt1.y, pt2.y)
        minZ = minOf(pt0.z, pt1.z, pt2.z)
        maxX = maxOf(pt0.x, pt1.x, pt2.x)
        maxY = maxOf(pt0.y, pt1.y, pt2.y)
        maxZ = maxOf(pt0.z, pt1.z, pt2.z)
    }

    open fun hitDistance(ray: RayF): Float {
        ray.origin.subtract(pt0, tmpS)
        ray.direction.cross(e2, tmpP)
        tmpS.cross(e1, tmpQ)

        val f = 1f / tmpP.dot(e1)
        val t = f * tmpQ.dot(e2)
        val u = f * tmpP.dot(tmpS)
        val v = f * tmpQ.dot(ray.direction)

        return if (u >= 0f && v >= 0f && u + v <= 1f && t >= 0f) t else Float.MAX_VALUE
    }

    companion object {
        fun getTriangles(meshData: IndexedVertexList): List<Triangle> {
            val triangles = mutableListOf<Triangle>()
            for (i in 0 until meshData.numIndices step 3) {
                triangles += Triangle(meshData, i)
            }
            return triangles
        }
    }
}