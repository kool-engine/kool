package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.*
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.scopedMem
import physxandroid.PxTopLevelFunctions
import physxandroid.geometry.PxTriangleMesh
import physxandroid.geometry.PxTriangleMeshGeometry
import physxandroid.support.PxArray_PxU32
import physxandroid.support.PxArray_PxVec3

// GENERATED CODE BELOW:
// Transformed from desktop source

actual fun TriangleMesh(geometry: IndexedVertexList<*>): TriangleMesh = TriangleMeshImpl(geometry)

val TriangleMesh.pxTriangleMesh: PxTriangleMesh get() = (this as TriangleMeshImpl).pxTriangleMesh

class TriangleMeshImpl(override val geometry: IndexedVertexList<*>) : TriangleMesh() {

    val pxTriangleMesh: PxTriangleMesh

    override var releaseWithGeometry = true
    internal var refCnt = 0

    init {
        PhysicsImpl.checkIsLoaded()
        scopedMem {
            val pointVector = PxArray_PxVec3()
            val indexVector = PxArray_PxU32()
            val pxVec3 = createPxVec3()
            geometry.forEach {
                pointVector.pushBack(it.toPxVec3(pxVec3))
            }
            for (i in 0 until geometry.numIndices) {
                indexVector.pushBack(geometry.indices[i])
            }

            // create mesh descriptor
            val points = createPxBoundedData()
            points.count = pointVector.size()
            points.stride = SIZEOF.PxVec3
            points.data = pointVector.begin()

            val triangles = createPxBoundedData()
            triangles.count = indexVector.size() / 3
            triangles.stride = 12
            triangles.data = indexVector.begin()

            val desc = createPxTriangleMeshDesc()
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
    override fun doRelease() {
        pxTriangleMesh.release()
    }
}

class TriangleMeshGeometryImpl(override val triangleMesh: TriangleMesh, override val scale: Vec3f) : CollisionGeometryImpl(), TriangleMeshGeometry {
    constructor(geometry: IndexedVertexList<*>, scale: Vec3f) : this(TriangleMesh(geometry), scale)

    override val pxGeometry: PxTriangleMeshGeometry

    init {
        PhysicsImpl.checkIsLoaded()
        scopedMem {
            val s = scale.toPxVec3(createPxVec3())
            val r = createPxQuat(0f, 0f, 0f, 1f)
            val meshScale = createPxMeshScale(s, r)
            pxGeometry = PxTriangleMeshGeometry(triangleMesh.pxTriangleMesh, meshScale)
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

    override fun doRelease() {
        super.doRelease()
        if (triangleMesh.releaseWithGeometry) {
            triangleMesh.pxTriangleMesh.release()
        }
    }
}