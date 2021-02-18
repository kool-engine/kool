package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.Releasable
import de.fabmax.kool.physics.toVec3f
import de.fabmax.kool.physics.toVector_PxVec3
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.util.IndexedVertexList
import physx.*

actual class ConvexMesh actual constructor(actual val points: List<Vec3f>) : Releasable {

    actual val convexHull: IndexedVertexList

    val pxConvexMesh: PxConvexMesh

    actual var releaseWithGeometry = true
    internal var refCnt = 0

    init {
        val vec3Vector = points.toVector_PxVec3()
        val desc = PxConvexMeshDesc()
        val flags = PxConvexFlags(PxConvexFlagEnum.eCOMPUTE_CONVEX.toShort())
        desc.flags = flags
        desc.points.count = points.size
        desc.points.stride = 12
        desc.points.data = vec3Vector.data()
        pxConvexMesh = Physics.cooking.createConvexMesh(desc, Physics.physics.getPhysicsInsertionCallback())

        PhysXJsLoader.destroy(vec3Vector)
        PhysXJsLoader.destroy(flags)
        PhysXJsLoader.destroy(desc)

        convexHull = makeConvexHull(pxConvexMesh)
    }

    private fun makeConvexHull(convexMesh: PxConvexMesh): IndexedVertexList {
        val geometry = IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS)

        val v = MutableVec3f()
        val polyIndices = mutableListOf<Int>()
        val poly = PxHullPolygon()
        for (i in 0 until convexMesh.getNbPolygons()) {
            polyIndices.clear()

            convexMesh.getPolygonData(i, poly)
            for (j in 0 until poly.mNbVerts) {
                val vi = Physics.TypeHelpers.getU8At(convexMesh.getIndexBuffer(), poly.mIndexBase + j).toInt() and 0xff
                val pt = Physics.TypeHelpers.getVec3At(convexMesh.getVertices(), vi)
                polyIndices += geometry.addVertex(pt.toVec3f(v))
            }

            for (j in 2 until polyIndices.size) {
                val v0 = polyIndices[0]
                val v1 = polyIndices[j - 1]
                val v2 = polyIndices[j]
                geometry.addTriIndices(v0, v1, v2)
            }
        }
        PhysXJsLoader.destroy(poly)
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