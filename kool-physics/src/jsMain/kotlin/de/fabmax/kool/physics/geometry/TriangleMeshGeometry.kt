package de.fabmax.kool.physics.geometry

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.toPxVec3
import de.fabmax.kool.util.IndexedVertexList
import physx.*

actual class TriangleMeshGeometry actual constructor(triangleMesh: TriangleMesh, scale: Vec3f) : CommonTriangleMeshGeometry(triangleMesh), CollisionGeometry {

    override val pxGeometry: PxTriangleMeshGeometry

    init {
        Physics.checkIsLoaded()
        val s = scale.toPxVec3(PxVec3())
        val r = PxQuat(0f, 0f, 0f, 1f)
        val meshScale = PxMeshScale(s, r)
        pxGeometry = PxTriangleMeshGeometry(triangleMesh.pxTriangleMesh, meshScale)
        PhysXJsLoader.destroy(s, r, meshScale)

        if (triangleMesh.releaseWithGeometry) {
            if (triangleMesh.refCnt > 0) {
                // PxTriangleMesh starts with a ref count of 1, only increment it if this is not the first
                // geometry which uses it
                triangleMesh.pxTriangleMesh.acquireReference()
            }
            triangleMesh.refCnt++
        }
    }

    actual constructor(geometry: IndexedVertexList) : this(TriangleMesh(geometry))

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        if (triangleMesh.releaseWithGeometry) {
            triangleMesh.pxTriangleMesh.release()
        }
    }
}