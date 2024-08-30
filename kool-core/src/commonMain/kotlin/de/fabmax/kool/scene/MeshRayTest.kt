package de.fabmax.kool.scene

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.*
import de.fabmax.kool.scene.geometry.PrimitiveType
import kotlin.math.sqrt

interface MeshRayTest {

    fun rayTest(test: RayTest, localRay: RayF): Boolean
    fun onMeshDataChanged(mesh: Mesh) { }

    companion object {
        fun nopTest(): MeshRayTest = object : MeshRayTest {
            override fun rayTest(test: RayTest, localRay: RayF) = false
        }

        fun boundsTest(): MeshRayTest = object : MeshRayTest {
            var mesh: Mesh? = null

            private val tmpVec = MutableVec3f()

            override fun onMeshDataChanged(mesh: Mesh) {
                this.mesh = mesh
            }

            override fun rayTest(test: RayTest, localRay: RayF): Boolean {
                val mesh = this.mesh ?: return false
                val distSqr = mesh.geometry.bounds.hitDistanceSqr(localRay)
                if (distSqr < Float.POSITIVE_INFINITY) {
                    tmpVec.set(localRay.direction).mul(sqrt(distSqr))
                    val globalDist = mesh.toGlobalCoords(tmpVec, 0f).length()
                    if (globalDist <= test.hitDistance) {
                        test.setHit(mesh, globalDist.toDouble())
                        return true
                    }
                }
                return false
            }
        }

        fun geometryTest(mesh: Mesh): MeshRayTest {
            return when (mesh.geometry.primitiveType) {
                PrimitiveType.TRIANGLES -> TriangleGeometry(mesh)
                PrimitiveType.LINES -> LineGeometry(mesh)
                else -> throw IllegalArgumentException("Mesh primitive type must be either GL_TRIANGLES or GL_LINES")
            }
        }
    }

    class TriangleGeometry(val mesh: Mesh) : MeshRayTest {
        var triangleTree: KdTree<Triangle>? = null; private set

        private val rayD = RayD()
        private val normalF = MutableVec3f()
        private val rayTraverser = TriangleHitTraverser<Triangle>()

        override fun onMeshDataChanged(mesh: Mesh) {
            triangleTree = if (mesh.geometry.primitiveType == PrimitiveType.TRIANGLES) {
                triangleKdTree(Triangle.getTriangles(mesh.geometry))
            } else {
                null
            }
        }

        override fun rayTest(test: RayTest, localRay: RayF): Boolean {
            rayTraverser.setup(localRay.toRayD(rayD))
            if (triangleTree == null) {
                onMeshDataChanged(mesh)
            }
            triangleTree?.let { rayTraverser.traverse(it) }

            rayTraverser.nearest?.let { hitTri ->
                // hit
                val globalHit = mesh.toGlobalCoords(rayTraverser.hitPoint)
                val globalDist = globalHit.distance(test.ray.origin)
                if (globalDist < test.hitDistance) {
                    test.setHit(mesh, globalHit)
                    mesh.toGlobalCoords(hitTri.e1.cross(hitTri.e2, test.hitNormalGlobal.toMutableVec3f(normalF)), 0f)
                    return true
                }
            }
            return false
        }
    }

    class LineGeometry(val mesh: Mesh) : MeshRayTest {
        var edgeTree: KdTree<Edge<Vec3f>>? = null; private set

        private val rayTraverser = NearestEdgeToRayTraverser<Edge<Vec3f>>()
        private val tmpVec = MutableVec3f()
        private val rayD = RayD()

        override fun onMeshDataChanged(mesh: Mesh) {
            edgeTree = if (mesh.geometry.primitiveType == PrimitiveType.LINES) {
                edgeKdTree(Edge.getEdges(mesh.geometry))
            } else {
                null
            }
        }

        override fun rayTest(test: RayTest, localRay: RayF): Boolean {
            rayTraverser.setup(localRay.toRayD(rayD))
            if (edgeTree == null) {
                onMeshDataChanged(mesh)
            }
            edgeTree?.let { rayTraverser.traverse(it) }

            if (rayTraverser.nearest != null) {
                tmpVec.set(localRay.direction).mul(sqrt(rayTraverser.distanceSqr).toFloat())
                val globalDist = mesh.toGlobalCoords(tmpVec, 0f).length()
                if (globalDist < test.hitDistance) {
                    test.setHit(mesh, globalDist.toDouble())
                    return true
                }
            }
            return false
        }
    }
}
