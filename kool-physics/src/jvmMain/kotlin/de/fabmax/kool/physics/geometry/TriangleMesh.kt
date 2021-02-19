package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.*
import de.fabmax.kool.util.IndexedVertexList
import org.lwjgl.system.MemoryStack
import physx.common.PxVec3
import physx.geomutils.PxTriangleMesh
import physx.support.Vector_PxU32
import physx.support.Vector_PxVec3

actual class TriangleMesh actual constructor(actual val geometry: IndexedVertexList) : Releasable {

    val pxTriangleMesh: PxTriangleMesh

    actual var releaseWithGeometry = true
    internal var refCnt = 0

    init {
        Physics.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val pointVector = Vector_PxVec3()
            val indexVector = Vector_PxU32()
            val pxVec3 = mem.createPxVec3()
            geometry.forEach {
                pointVector.push_back(it.toPxVec3(pxVec3))
            }
            for (i in 0 until geometry.numIndices) {
                indexVector.push_back(geometry.indices[i])
            }

            // create mesh descriptor
            val points = mem.createPxBoundedData()
            points.count = pointVector.size()
            points.stride = PxVec3.SIZEOF
            points.data = pointVector.data()

            val triangles = mem.createPxBoundedData()
            triangles.count = indexVector.size() / 3
            triangles.stride = 12
            triangles.data = indexVector.data()

            val desc = mem.createPxTriangleMeshDesc()
            desc.points = points
            desc.triangles = triangles

            // cook mesh
            pxTriangleMesh = Physics.cooking.createTriangleMesh(desc, Physics.physics.physicsInsertionCallback)

            pointVector.destroy()
            indexVector.destroy()
        }
    }

    /**
     * Only use this if [releaseWithGeometry] is false. Releases the underlying PhysX mesh.
     */
    override fun release() {
        pxTriangleMesh.release()
    }
}