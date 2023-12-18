package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.createPxMeshScale
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.BaseReleasable
import org.lwjgl.system.MemoryStack
import physx.geometry.*

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual typealias GeometryHolder = PxGeometry

actual fun BoxGeometry(size: Vec3f) : BoxGeometry = BoxGeometryImpl(size)
actual fun CapsuleGeometry(height: Float, radius: Float): CapsuleGeometry = CapsuleGeometryImpl(height, radius)
actual fun ConvexMeshGeometry(convexMesh: ConvexMesh, scale: Vec3f): ConvexMeshGeometry = ConvexMeshGeometryImpl(convexMesh, scale)
actual fun ConvexMeshGeometry(points: List<Vec3f>, scale: Vec3f): ConvexMeshGeometry = ConvexMeshGeometryImpl(points, scale)
actual fun CylinderGeometry(length: Float, radius: Float): CylinderGeometry = CylinderGeometryImpl(length, radius)
actual fun HeightFieldGeometry(heightField: HeightField): HeightFieldGeometry = HeightFieldGeometryImpl(heightField)
actual fun PlaneGeometry(): CommonPlaneGeometry = PlaneGeometryImpl()
actual fun SphereGeometry(radius: Float): SphereGeometry = SphereGeometryImpl(radius)
actual fun TriangleMeshGeometry(triangleMesh: TriangleMesh, scale: Vec3f): TriangleMeshGeometry = TriangleMeshGeometryImpl(triangleMesh, scale)
actual fun TriangleMeshGeometry(geometry: IndexedVertexList, scale: Vec3f): TriangleMeshGeometry = TriangleMeshGeometryImpl(geometry, scale)

abstract class CollisionGeometryImpl : BaseReleasable(), CollisionGeometry {
    init { PhysicsImpl.checkIsLoaded() }

    override fun release() {
        super.release()
        holder.destroy()
    }
}

class BoxGeometryImpl(override val size: Vec3f) : CollisionGeometryImpl(), BoxGeometry {
    override val holder: PxBoxGeometry = PxBoxGeometry(size.x * 0.5f, size.y * 0.5f, size.z * 0.5f)
}

class CapsuleGeometryImpl(override val height: Float, override val radius: Float) : CollisionGeometryImpl(), CapsuleGeometry {
    override val holder: PxCapsuleGeometry = PxCapsuleGeometry(radius, height / 2f)
}

class CylinderGeometryImpl(override val length: Float, override val radius: Float) : CollisionGeometryImpl(), CylinderGeometry {
    override val holder: PxConvexMeshGeometry = MemoryStack.stackPush().use { mem ->
        PxConvexMeshGeometry(PhysicsImpl.unitCylinder, mem.createPxMeshScale(Vec3f(length, radius, radius)))
    }
}

class PlaneGeometryImpl : CollisionGeometryImpl(), CommonPlaneGeometry {
    override val holder: PxPlaneGeometry = PxPlaneGeometry()
}

class SphereGeometryImpl(override val radius: Float) : CollisionGeometryImpl(), SphereGeometry {
    override val holder: PxSphereGeometry = PxSphereGeometry(radius)
}
