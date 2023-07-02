package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.geometry.IndexedVertexList
import org.lwjgl.system.MemoryStack
import physx.PxTopLevelFunctions
import physx.common.PxVec3
import physx.cooking.PxConvexFlagEnum
import physx.geometry.PxConvexMesh
import physx.support.NativeArrayHelpers

actual class ConvexMesh actual constructor(actual val points: List<Vec3f>) : Releasable {

    actual val convexHull: IndexedVertexList

    val pxConvexMesh: PxConvexMesh

    actual var releaseWithGeometry = true
    internal var refCnt = 0

    init {
        Physics.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val vec3Vector = points.toVector_PxVec3()
            val desc = mem.createPxConvexMeshDesc()
            desc.flags = mem.createPxConvexFlags(PxConvexFlagEnum.eCOMPUTE_CONVEX.value)
            desc.points.count = points.size
            desc.points.stride = PxVec3.SIZEOF
            desc.points.data = vec3Vector.data()
            pxConvexMesh = PxTopLevelFunctions.CreateConvexMesh(Physics.cookingParams, desc)

            vec3Vector.destroy()

            convexHull = makeConvexHull(mem, pxConvexMesh)
        }
    }

    private fun makeConvexHull(mem: MemoryStack, convexMesh: PxConvexMesh): IndexedVertexList {
        val geometry = IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS)

        val v = MutableVec3f()
        val polyIndices = mutableListOf<Int>()
        val poly = mem.createPxHullPolygon()
        for (i in 0 until convexMesh.nbPolygons) {
            polyIndices.clear()

            convexMesh.getPolygonData(i, poly)
            for (j in 0 until poly.mNbVerts) {
                val vi = NativeArrayHelpers.getU8At(convexMesh.indexBuffer, poly.mIndexBase + j).toInt() and 0xff
                val pt = PxVec3.arrayGet(convexMesh.vertices.address, vi)
                polyIndices += geometry.addVertex(pt.toVec3f(v))
            }

            for (j in 2 until polyIndices.size) {
                val v0 = polyIndices[0]
                val v1 = polyIndices[j - 1]
                val v2 = polyIndices[j]
                geometry.addTriIndices(v0, v1, v2)
            }
        }
        geometry.generateNormals()
        return geometry
    }

    /**
     * Only use this if [releaseWithGeometry] is false. Releases the underlying PhysX mesh.
     */
    override fun release() {
        pxConvexMesh.release()
    }
}