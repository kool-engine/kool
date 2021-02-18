package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.Releasable
import de.fabmax.kool.physics.toPxVec3
import de.fabmax.kool.util.IndexedVertexList
import physx.*

actual class TriangleMesh actual constructor(actual val geometry: IndexedVertexList) : Releasable {

    val pxTriangleMesh: PxTriangleMesh

    actual var releaseWithGeometry = true
    internal var refCnt = 0

    init {
        val pointVector = Vector_PxVec3()
        val indexVector = Vector_PxU32()
        val pxVec3 = PxVec3()
        geometry.forEach {
            pointVector.push_back(it.toPxVec3(pxVec3))
        }
        for (i in 0 until geometry.numIndices) {
            indexVector.push_back(geometry.indices[i])
        }

        // create mesh descriptor
        val points = PxBoundedData()
        points.count = pointVector.size()
        points.stride = 12
        points.data = pointVector.data()

        val triangles = PxBoundedData()
        triangles.count = indexVector.size() / 3
        triangles.stride = 12
        triangles.data = indexVector.data()

        val desc = PxTriangleMeshDesc()
        desc.points = points
        desc.triangles = triangles

        pxTriangleMesh = Physics.cooking.createTriangleMesh(desc, Physics.physics.getPhysicsInsertionCallback())

        PhysXJsLoader.destroy(pointVector)
        PhysXJsLoader.destroy(indexVector)
        PhysXJsLoader.destroy(pxVec3)
        PhysXJsLoader.destroy(points)
        PhysXJsLoader.destroy(triangles)
        PhysXJsLoader.destroy(desc)
    }

    /**
     * Only use this if [releaseWithGeometry] is false. Releases the underlying PhysX mesh.
     */
    override fun release() {
        pxTriangleMesh.release()
    }
}