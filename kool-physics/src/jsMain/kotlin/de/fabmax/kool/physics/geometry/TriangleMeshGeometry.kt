package de.fabmax.kool.physics.geometry

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.MemoryStack
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.toPxVec3
import de.fabmax.kool.scene.geometry.IndexedVertexList
import physx.PxTriangleMeshGeometry
import physx.destroy

actual class TriangleMeshGeometry actual constructor(triangleMesh: TriangleMesh, scale: Vec3f) : CommonTriangleMeshGeometry(triangleMesh), CollisionGeometry {

    override val pxGeometry: PxTriangleMeshGeometry

    init {
        Physics.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val s = scale.toPxVec3(mem.createPxVec3())
            val r = mem.createPxQuat(0f, 0f, 0f, 1f)
            val meshScale = mem.createPxMeshScale(s, r)
            pxGeometry = PxTriangleMeshGeometry(triangleMesh.pxTriangleMesh, meshScale)
        }

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

    actual override fun release() = pxGeometry.destroy()
}