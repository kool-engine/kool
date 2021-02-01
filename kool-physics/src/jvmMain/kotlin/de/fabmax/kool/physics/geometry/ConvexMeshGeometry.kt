package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.toVec3f
import de.fabmax.kool.physics.toVector_PxVec3
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.util.IndexedVertexList
import physx.PxTopLevelFunctions
import physx.cooking.PxConvexFlagEnum
import physx.cooking.PxConvexFlags
import physx.cooking.PxConvexMeshDesc
import physx.geomutils.PxConvexMesh
import physx.geomutils.PxConvexMeshGeometry
import physx.geomutils.PxGeometry
import physx.geomutils.PxHullPolygon

actual class ConvexMeshGeometry actual constructor(points: List<Vec3f>) : CommonConvexMeshGeometry(points), CollisionGeometry {

    val pxMesh: PxConvexMesh

    override val pxGeometry: PxGeometry
    override val convexHull: IndexedVertexList

    init {
        pxMesh = toConvexMesh(points)
        pxGeometry = PxConvexMeshGeometry(pxMesh)
        convexHull = makeMeshData(pxMesh)
    }

    companion object {
        fun toConvexMesh(points: List<Vec3f>): PxConvexMesh {
            val vec3Vector = points.toVector_PxVec3()
            val desc = PxConvexMeshDesc()
            desc.flags = PxConvexFlags(PxConvexFlagEnum.eCOMPUTE_CONVEX.toShort())
            desc.points.count = points.size
            desc.points.stride = 3 * 4      // point consists of 3 floats with 4 bytes each
            desc.points.data = vec3Vector.data()
            return Physics.cooking.createConvexMesh(desc, Physics.physics.getPhysicsInsertionCallback())
        }

        fun makeMeshData(convexMesh: PxConvexMesh): IndexedVertexList {
            val geometry = IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS)

            val v = MutableVec3f()
            val polyIndices = mutableListOf<Int>()
            val poly = PxHullPolygon()
            for (i in 0 until convexMesh.getNbPolygons()) {
                polyIndices.clear()

                convexMesh.getPolygonData(i, poly)
                for (j in 0 until poly.mNbVerts) {
                    val vi = PxTopLevelFunctions.getU8At(convexMesh.getIndexBuffer(), poly.mIndexBase + j)
                    val pt = PxTopLevelFunctions.getVec3At(convexMesh.getVertices(), vi)
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
    }
}