package de.fabmax.kool.physics.geometry

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.toPxVec3
import physx.*

actual class ConvexMeshGeometry actual constructor(convexMesh: ConvexMesh, scale: Vec3f) : CommonConvexMeshGeometry(convexMesh), CollisionGeometry {

    override val pxGeometry: PxGeometry

    init {
        Physics.checkIsLoaded()
        val s = scale.toPxVec3(PxVec3())
        val r = PxQuat(0f, 0f, 0f, 1f)
        val meshScale = PxMeshScale(s, r)
        pxGeometry = PxConvexMeshGeometry(convexMesh.pxConvexMesh, meshScale)
        PhysXJsLoader.destroy(s, r, meshScale)

        if (convexMesh.releaseWithGeometry) {
            if (convexMesh.refCnt > 0) {
                // PxConvexMesh starts with a ref count of 1, only increment it if this is not the first
                // geometry which uses it
                convexMesh.pxConvexMesh.acquireReference()
            }
            convexMesh.refCnt++
        }
    }

    actual constructor(points: List<Vec3f>) : this(ConvexMesh(points))

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        if (convexMesh.releaseWithGeometry) {
            convexMesh.pxConvexMesh.release()
        }
    }
}