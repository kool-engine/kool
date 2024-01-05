package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.*
import de.fabmax.kool.scene.geometry.IndexedVertexList
import org.lwjgl.system.MemoryStack
import physx.PxTopLevelFunctions
import physx.common.PxVec3
import physx.geometry.PxTriangleMesh
import physx.geometry.PxTriangleMeshGeometry
import physx.support.PxArray_PxU32
import physx.support.PxArray_PxVec3

actual fun TriangleMesh(geometry: IndexedVertexList): TriangleMesh = TriangleMeshImpl(geometry)

val TriangleMesh.pxTriangleMesh: PxTriangleMesh get() = (this as TriangleMeshImpl).pxTriangleMesh

class TriangleMeshImpl(override val geometry: IndexedVertexList) : TriangleMesh() {

    val pxTriangleMesh: PxTriangleMesh

    override var releaseWithGeometry = true
    internal var refCnt = 0

    init {
        PhysicsImpl.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val pointVector = PxArray_PxVec3()
            val indexVector = PxArray_PxU32()
            val pxVec3 = mem.createPxVec3()
            geometry.forEach {
                pointVector.pushBack(it.toPxVec3(pxVec3))
            }
            for (i in 0 until geometry.numIndices) {
                indexVector.pushBack(geometry.indices[i])
            }

            // create mesh descriptor
            val points = mem.createPxBoundedData()
            points.count = pointVector.size()
            points.stride = PxVec3.SIZEOF
            points.data = pointVector.begin()

            val triangles = mem.createPxBoundedData()
            triangles.count = indexVector.size() / 3
            triangles.stride = 12
            triangles.data = indexVector.begin()

            val desc = mem.createPxTriangleMeshDesc()
            desc.points = points
            desc.triangles = triangles

            // cook mesh
            pxTriangleMesh = PxTopLevelFunctions.CreateTriangleMesh(PhysicsImpl.cookingParams, desc)

            pointVector.destroy()
            indexVector.destroy()
        }
    }

    /**
     * Only use this if [releaseWithGeometry] is false. Releases the underlying PhysX mesh.
     */
    override fun release() {
        super.release()
        pxTriangleMesh.release()
    }
}

class TriangleMeshGeometryImpl(override val triangleMesh: TriangleMesh, override val scale: Vec3f) : CollisionGeometryImpl(), TriangleMeshGeometry {
    constructor(geometry: IndexedVertexList, scale: Vec3f) : this(TriangleMesh(geometry), scale)

    override val holder: PxTriangleMeshGeometry

    init {
        PhysicsImpl.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val s = scale.toPxVec3(mem.createPxVec3())
            val r = mem.createPxQuat(0f, 0f, 0f, 1f)
            val meshScale = mem.createPxMeshScale(s, r)
            holder = PxTriangleMeshGeometry(triangleMesh.pxTriangleMesh, meshScale)
        }

        if (triangleMesh.releaseWithGeometry) {
            triangleMesh as TriangleMeshImpl
            if (triangleMesh.refCnt > 0) {
                // PxTriangleMesh starts with a ref count of 1, only increment it if this is not the first
                // geometry which uses it
                triangleMesh.pxTriangleMesh.acquireReference()
            }
            triangleMesh.refCnt++
        }
    }

    override fun release() {
        super.release()
        if (triangleMesh.releaseWithGeometry) {
            triangleMesh.pxTriangleMesh.release()
        }
    }
}
