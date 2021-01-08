package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.IndexedVertexList
import physx.*
import physx.PhysX.toPxVec3Vector

actual class ConvexHullShape actual constructor(points: List<Vec3f>) : CommonConvexHullShape(points), CollisionShape {

    private val pxMesh: PxConvexMesh
    private val bounds = BoundingBox()

    override val geometry: IndexedVertexList

    init {
        Physics.checkIsLoaded()

        pxMesh = toConvexMesh(points)
        geometry = putMeshData(pxMesh)

        bounds.add(points)
    }

    override fun getAabb(result: BoundingBox) = result.set(bounds)
    override fun getBoundingSphere(result: MutableVec4f) = result.set(bounds.center, bounds.size.length() / 2)

    override fun attachTo(actor: PxRigidActor, material: PxMaterial, flags: PxShapeFlags, collisionFilter: PxFilterData): PxShape {
        val scaling = PhysX.PxMeshScale(Vec3f(1f).toPxVec3(), Vec4f(0f, 0f, 0f, 1f).toPxQuat())
        val meshFlags = PhysX.PxConvexMeshGeometryFlags(PhysX.PxConvexMeshGeometryFlag.eTIGHT_BOUNDS.value)
        val geometry = PhysX.PxConvexMeshGeometry(pxMesh, scaling, meshFlags)
        val shape = PhysX.physics.createShape(geometry, material, true, flags)
        shape.setSimulationFilterData(collisionFilter)
        actor.attachShape(shape)
        return shape
    }

    companion object {
        fun toConvexMesh(points: List<Vec3f>): PxConvexMesh {
            val vec3Vector = points.toPxVec3Vector()
            val mesh = PhysX.createConvexMesh(vec3Vector, PhysX.cooking, PhysX.physics)
            vec3Vector.delete()
            return mesh
        }

        fun putMeshData(convexMesh: PxConvexMesh): IndexedVertexList {
            val geometry = IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS)

            val v = MutableVec3f()
            val polyIndices = mutableListOf<Int>()
            for (i in 0 until convexMesh.getNbPolygons()) {
                polyIndices.clear()

                val attribs = PhysX.PxConvexMesh_getPolyAttribs(convexMesh, i)
                val nbVerts = (attribs shr 16) and 0xffff
                val indexBase = attribs and 0xffff
                for (j in 0 until nbVerts) {
                    val vi = PhysX.PxConvexMesh_getIndex(convexMesh, indexBase + j)
                    PhysX.PxConvexMesh_getVertex(convexMesh, vi).toVec3f(v)
                    polyIndices += geometry.addVertex(v)
                }

                for (j in 2 until polyIndices.size) {
                    val v0 = polyIndices[0]
                    val v1 = polyIndices[j - 1]
                    val v2 = polyIndices[j]
                    geometry.addTriIndices(v0, v1, v2)
                }
            }
            geometry.generateNormals()
            return geometry
        }
    }

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        // rough approximation: use inertia of bounding box
        result.x = (mass / 12f) * (bounds.size.y * bounds.size.y + bounds.size.z * bounds.size.z)
        result.z = (mass / 12f) * (bounds.size.x * bounds.size.x + bounds.size.y * bounds.size.y)
        result.y = (mass / 12f) * (bounds.size.x * bounds.size.x + bounds.size.z * bounds.size.z)
        return result
    }
}