package de.fabmax.kool.physics.geometry

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.createPxMeshScale
import de.fabmax.kool.physics.createPxQuat
import de.fabmax.kool.physics.createPxVec3
import de.fabmax.kool.physics.toPxVec3
import org.lwjgl.system.MemoryStack
import physx.geomutils.PxConvexMeshGeometry
import physx.geomutils.PxGeometry

actual class ConvexMeshGeometry actual constructor(convexMesh: ConvexMesh, scale: Vec3f) : CommonConvexMeshGeometry(convexMesh), CollisionGeometry {

    override val pxGeometry: PxGeometry

    init {
        MemoryStack.stackPush().use { mem ->
            val s = scale.toPxVec3(mem.createPxVec3())
            val r = mem.createPxQuat(0f, 0f, 0f, 1f)
            val meshScale = mem.createPxMeshScale(s, r)
            pxGeometry = PxConvexMeshGeometry(convexMesh.pxConvexMesh, meshScale)
        }

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