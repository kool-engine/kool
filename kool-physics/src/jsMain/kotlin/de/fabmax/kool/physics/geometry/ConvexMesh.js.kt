package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.geometry.IndexedVertexList
import physx.*

actual fun ConvexMesh(points: List<Vec3f>): ConvexMesh = ConvexMeshImpl(points)

val ConvexMesh.pxConvexMesh: PxConvexMesh get() = (this as ConvexMeshImpl).pxConvexMesh

class ConvexMeshImpl(override val points: List<Vec3f>, override var releaseWithGeometry: Boolean = true) : ConvexMesh() {

    override val convexHull: IndexedVertexList

    val pxConvexMesh: PxConvexMesh

    internal var refCnt = 0

    init {
        PhysicsImpl.checkIsLoaded()
        pxConvexMesh = makePxConvexMesh(points)
        convexHull = makeConvexHull(pxConvexMesh)
    }

    private fun makeConvexHull(convexMesh: PxConvexMesh): IndexedVertexList {
        return MemoryStack.stackPush().use { mem ->
            val geometry = IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS)
            val v = MutableVec3f()
            val polyIndices = mutableListOf<Int>()
            val poly = mem.createPxHullPolygon()
            for (i in 0 until convexMesh.nbPolygons) {
                polyIndices.clear()

                convexMesh.getPolygonData(i, poly)
                for (j in 0 until poly.mNbVerts) {
                    val vi = NativeArrayHelpers.getU8At(convexMesh.indexBuffer, poly.mIndexBase + j).toInt() and 0xff
                    val pt = NativeArrayHelpers.getVec3At(convexMesh.vertices, vi)
                    polyIndices += geometry.addVertex(pt.toVec3f(v))
                }

                for (j in 2 until polyIndices.size) {
                    val v0 = polyIndices[0]
                    val v1 = polyIndices[j - 1]
                    val v2 = polyIndices[j]
                    geometry.addTriIndices(v0, v1, v2)
                }
            }
            geometry.generateNormals()
            geometry
        }
    }

    /**
     * Only use this if [releaseWithGeometry] is false. Releases the underlying PhysX mesh.
     */
    override fun release() {
        super.release()
        pxConvexMesh.release()
    }

    companion object {
        internal fun makePxConvexMesh(points: List<Vec3f>): PxConvexMesh = MemoryStack.stackPush().use { mem ->
            val vec3Vector = points.toPxArray_PxVec3()
            val desc = mem.createPxConvexMeshDesc()
            desc.flags = mem.createPxConvexFlags(PxConvexFlagEnum.eCOMPUTE_CONVEX)
            desc.points.count = points.size
            desc.points.stride = 12
            desc.points.data = vec3Vector.begin()
            val pxConvexMesh = PxTopLevelFunctions.CreateConvexMesh(PhysicsImpl.cookingParams, desc)
            vec3Vector.destroy()
            pxConvexMesh
        }
    }
}

class ConvexMeshGeometryImpl(override val convexMesh: ConvexMesh, override val scale: Vec3f) : CollisionGeometryImpl(), ConvexMeshGeometry {
    constructor(points: List<Vec3f>, scale: Vec3f) : this(ConvexMesh(points), scale)

    override val pxGeometry: PxConvexMeshGeometry

    init {
        PhysicsImpl.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val s = scale.toPxVec3(mem.createPxVec3())
            val r = mem.createPxQuat(0f, 0f, 0f, 1f)
            val meshScale = mem.createPxMeshScale(s, r)
            pxGeometry = PxConvexMeshGeometry(convexMesh.pxConvexMesh, meshScale)
        }

        if (convexMesh.releaseWithGeometry) {
            convexMesh as ConvexMeshImpl
            if (convexMesh.refCnt > 0) {
                // PxConvexMesh starts with a ref count of 1, only increment it if this is not the first
                // geometry which uses it
                convexMesh.pxConvexMesh.acquireReference()
            }
            convexMesh.refCnt++
        }
    }

    override fun release() {
        super.release()
        if (convexMesh.releaseWithGeometry) {
            convexMesh.pxConvexMesh.release()
        }
    }
}
