package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.MemoryStack
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.BaseReleasable
import physx.*

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class GeometryHolder(val px: PxGeometry)

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
    abstract val pxGeometry: PxGeometry

    override val holder: GeometryHolder by lazy { GeometryHolder(pxGeometry) }

    init { PhysicsImpl.checkIsLoaded() }

    override fun release() {
        super.release()
        pxGeometry.destroy()
    }
}

class BoxGeometryImpl(override val size: Vec3f) : CollisionGeometryImpl(), BoxGeometry {
    override val pxGeometry: PxBoxGeometry = PxBoxGeometry(size.x * 0.5f, size.y * 0.5f, size.z * 0.5f)
}

class CapsuleGeometryImpl(override val height: Float, override val radius: Float) : CollisionGeometryImpl(), CapsuleGeometry {
    override val pxGeometry: PxCapsuleGeometry = PxCapsuleGeometry(radius, height / 2f)
}

class CylinderGeometryImpl(override val length: Float, override val radius: Float) : CollisionGeometryImpl(), CylinderGeometry {
    override val pxGeometry: PxConvexMeshGeometry = MemoryStack.stackPush().use { mem ->
        PxConvexMeshGeometry(PhysicsImpl.unitCylinder, mem.createPxMeshScale(Vec3f(length, radius, radius)))
    }
}

class PlaneGeometryImpl : CollisionGeometryImpl(), CommonPlaneGeometry {
    override val pxGeometry: PxPlaneGeometry = PxPlaneGeometry()
}

class SphereGeometryImpl(override val radius: Float) : CollisionGeometryImpl(), SphereGeometry {
    override val pxGeometry: PxSphereGeometry = PxSphereGeometry(radius)
}
